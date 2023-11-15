package com.mcmp.controller.service;

import com.mcmp.controller.domain.AppEntity;
import com.mcmp.controller.domain.InstanceEntity;
import com.mcmp.controller.dto.AppDTO;
import com.mcmp.controller.dto.ResultDTO;
import com.mcmp.controller.exceptionHandler.GlobalExceptionHandler;
import com.mcmp.controller.repository.AppRepository;
import com.mcmp.controller.repository.InstanceRepository;
import com.mcmp.controller.util.CommandExecutor;
import com.mcmp.controller.util.CommonUtil;
import com.mcmp.controller.util.PlaybookPath;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 10/11/23
* @class-description : service class for serviceApiController
*
**/
@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AppService {

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private CommandExecutor commandExecutor;


    /**
     * @date : 10/11/23
     * @author : Jihyeong Lee
     * @method-description : method for deploying webserver and db
     **/
    public Object deployApp(AppDTO appDTO) {

        // exception handler for public ip or service instance id is not null
        if(appDTO.getVmInstancePublicIp() == null || appDTO.getServiceInstanceId() == null){
            throw new GlobalExceptionHandler.WrongParameterException("invalid parameters");
        }

        // exception handler for service instance id is existed in tb_service_instance
        InstanceEntity instance = instanceRepository.findById(appDTO.getServiceInstanceId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + appDTO.getServiceInstanceId()));

        // exception handler for service instance id is existed in tb_application
        AppEntity findByServiceId = appRepository.findByServiceInstanceId(appDTO.getServiceInstanceId());
        log.info("service_instance_id : {}", findByServiceId);
        String ip = "ubuntu@"+appDTO.getVmInstancePublicIp();

        log.info("ip : {}", ip);
        // executing ansible playbook
        String output;
        if(findByServiceId == null){
            output = commandExecutor.executePlaybook(ip, PlaybookPath.DEPLOY_APPLICATION);
        } else {
            throw new GlobalExceptionHandler.DeployedException("Application already deployed");
        }

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("ansible-result : {}", resultDTO);
        AppEntity savedAppEntity;
        // exception handler for failed ansible executing
        if(resultDTO.getFailures() != 0){
            throw new RuntimeException("Failed Application Deployment");
        } else if (resultDTO.getUnreachable() != 0){
            throw new RuntimeException("Cannot access to remote instance");
        } else {
            // update database and return result
            try {
                var appEntity = new AppEntity();
                appEntity.setServiceInstanceId(appDTO.getServiceInstanceId());
                appEntity.setApplicationName("WEBSERVICE");
                appEntity.setApplicationType("WEB&DB");
                appEntity.setApplicationActivateYN("N");
                appEntity.setApplicationCreateDate(CommonUtil.getCurrentDate());

                savedAppEntity = appRepository.save(appEntity);

                if (savedAppEntity.getApplicationId() == null) {
                    return HttpStatus.NOT_IMPLEMENTED;
                } else {
                    return AppDTO.of(savedAppEntity);
                }
            } catch (DataAccessException e) {
                log.error(e.getMessage());
                throw new GlobalExceptionHandler.DatabaseUpdateException(e);
            }
        }
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for activating application which already deployed
    *
    **/
    public Object activation(AppDTO appDTO) {

        // checking parameter and exception handler for no parameter
        if(appDTO.getVmInstancePublicIp() == null){
            throw new RuntimeException("no parameter");
        }

        // checking application id is existing in tb_application
        AppEntity appEntity = appRepository.findById(appDTO.getApplicationId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + appDTO.getApplicationId()));

        // make ip variable for ansible executing
        String ip = "ubuntu@"+appDTO.getVmInstancePublicIp();

        // executing activation or deactivation ansible
        String output = null;
        if(appDTO.getApplicationActivateYN().equals("Y")){
            if(!appEntity.getApplicationActivateYN().equals("Y")){
                output = commandExecutor.executePlaybook(ip, PlaybookPath.ACTIVATION_SERVICE);
            } else{
                throw new GlobalExceptionHandler.DeployedException("Application already running");
            }
        } else {
            if(!appEntity.getApplicationActivateYN().equals("N")){
                output = commandExecutor.executePlaybook(ip, PlaybookPath.DEACTIVATION_SERVICE);
            } else {
                throw new GlobalExceptionHandler.DeployedException("Application already not running");
            }
        }

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("ansible-result : {}", resultDTO);

        // exception handler for ansible failed executing and update database
        AppEntity savedAppEntity;
        if (resultDTO.getFailures() != 0){
            throw new RuntimeException("Application activation Failed");
        } else if (resultDTO.getUnreachable() != 0) {
            throw new RuntimeException("Cannot access to remote instance");
        } else {
            try {
                if (appDTO.getApplicationActivateYN().equals("Y") && appEntity.getApplicationActivateYN().equals("N")) {
                    appEntity.setApplicationActivateYN("Y");
                } else {
                    appEntity.setApplicationActivateYN("N");
                }
            } catch (DataAccessException e) {
                log.error(e.getMessage());
                throw new GlobalExceptionHandler.DatabaseUpdateException(e);
            }
            savedAppEntity = appRepository.save(appEntity);
        }
        return AppDTO.of(savedAppEntity);
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for application health-checking
    *
    **/

    public AppDTO healthChecking(String serviceInstanceId, String appId) {

        // checking service_instance_id & application_id parameter
        // and exception handling if these parameters are null
        log.info("service_instance_id : {}", serviceInstanceId);
        log.info("application_id : {}", appId);
        if(serviceInstanceId == null || appId == null){
            throw new GlobalExceptionHandler.WrongParameterException("invalid parameter");
        }

        InstanceEntity instance = instanceRepository.findById(serviceInstanceId)
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + serviceInstanceId));

        AppEntity appEntity = appRepository.findById(appId)
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + appId));

        // make variable for ansible executing
        String ip = "ubuntu@"+instance.getPublicIp();
        log.info(ip);

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            String output = commandExecutor.executePlaybook(ip, PlaybookPath.HEALTH_CHECKING_SERVICE);
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // update database and return result
        AppEntity savedAppEntity;
        if (resultDTO.getFailures() != 0){
            appEntity.setApplicationActivateYN("N");
        } else if (resultDTO.getUnreachable() != 0) {
            appEntity.setApplicationActivateYN("N");
        } else {
            if(appEntity.getApplicationActivateYN().equals("Y")){
                return AppDTO.of(appEntity);
            } else {
                appEntity.setApplicationActivateYN("Y");
            }
        }
        savedAppEntity = appRepository.save(appEntity);
        return AppDTO.of(savedAppEntity);
    }
}


