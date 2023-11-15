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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RelationNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AcrossService {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private CommandExecutor commandExecutor;

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for deploying application on multiple cloud
    *
    **/
    public Object deployAcrossService(AppDTO appDTO) {

        //checking null value of public Ip
        if (appDTO.getVmInstancePublicIp() == null) {
            throw new RuntimeException("wrong parameter");
        }

        // exception handler for existing service_instance_id in tb_service_instance
        InstanceEntity instance = instanceRepository.findById(appDTO.getServiceInstanceId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + appDTO.getServiceInstanceId()));

        // checking service_instance_id exists where in tb_application
        AppEntity findByServiceId = appRepository.findByServiceInstanceId(appDTO.getServiceInstanceId());

        // getting public ip
        String ip = "ubuntu@"+appDTO.getVmInstancePublicIp();

        // get application type which type is "WEB" or else
        String appType = appDTO.getApplicationType();

        String output;
        // executing ansible with ip and proper playbook by if condition
        if (appType.equals("WEB") && findByServiceId == null) {
            output = commandExecutor.executePlaybook(ip, PlaybookPath.WEB_DEPLOY);
        } else if (appType.equals("DB")){
            output = commandExecutor.executePlaybook(ip, PlaybookPath.DB_DEPLOY);
        } else {
            output = commandExecutor.executePlaybook(ip, PlaybookPath.DEPLOY_APPLICATION);
        }

        // parsing ansible-result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // exception handler for failed ansible executing and db-update
        var appEntity = new AppEntity();
        AppEntity savedAppEntity;
        if (resultDTO.getFailures() != 0){
            throw new RuntimeException("Failed Application Deployment");
        } else if (resultDTO.getUnreachable() != 0) {
            throw new RuntimeException("Cannot access to remote instance");
        } else {
            try {
                appEntity.setServiceInstanceId(appDTO.getServiceInstanceId());
                if (appType.equals("WEB")) {
                    appEntity.setApplicationName("WEBSERVER");
                    appEntity.setApplicationType("WEB");
                } else if ("DB".equals(appType)){
                    appEntity.setApplicationName("DBSERVER");
                    appEntity.setApplicationType("DB");
                } else {
                    appEntity.setApplicationName("WEBSERVICE");
                    appEntity.setApplicationType("WEB&DB");
                }
                appEntity.setApplicationActivateYN("N");
                appEntity.setApplicationCreateDate(CommonUtil.getCurrentDate());
            } catch (DataAccessException e) {
                throw new GlobalExceptionHandler.DatabaseUpdateException(e);
            }
            // save db and return result
            savedAppEntity = appRepository.save(appEntity);
            if (savedAppEntity.getApplicationId() == null) {
                throw new NoSuchElementException("Data insert error");
            } else {
                return AppDTO.of(savedAppEntity);
            }
        }
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for activating application
    *
    **/
    public Object activation(List<AppDTO> appDTO) throws IOException {

        // making sure that parameter for activating application are more than two
        if (appDTO.size() < 2) {
            throw new IllegalArgumentException("At least two parameter need");
        }

        // checking activation or deactivation
        boolean checkActivation = appDTO.stream()
                .allMatch(a -> "Y".equals(a.getApplicationActivateYN()));

        // checking across-service-type is GSLB or VPN
        boolean acrossType = appDTO.stream().allMatch(x -> x.getAcrossType().equals("GSLB"));
        log.info(String.valueOf(acrossType));

        // get private ip for vpn
        List<String> dbPrivateIp = appDTO.stream()
                .filter(app -> "DB".equals(app.getApplicationType()))
                .map(AppDTO::getVmInstancePrivateIp)
                .collect(Collectors.toList());
        // get database public ip for vpn
        List<String> dbPublicIp = appDTO.stream()
                .filter(app -> "DB".equals(app.getApplicationType()))
                .map(app -> "ubuntu@"+app.getVmInstancePublicIp())
                .collect(Collectors.toList());
        // get webserver public ip for vpn
        List<String> webPublicIp = appDTO.stream()
                .filter(app -> "WEB".equals(app.getApplicationType()))
                .map(app -> "ubuntu@"+app.getVmInstancePublicIp())
                .collect(Collectors.toList());
        // get public ip for gslb
        List<String> servicePublicIp = appDTO.stream()
                .filter(app -> "WEB&DB".equals(app.getApplicationType()))
                .map(app -> "ubuntu@"+app.getVmInstancePublicIp())
                .collect(Collectors.toList());
        // make list to string
        String dbIp = String.join(",", dbPublicIp);
        String PrivateIpDb = String.join(",", dbPrivateIp);
        String webIp = String.join(",", webPublicIp);
        String serviceIp =  String.join(",", servicePublicIp);

        log.info("DB IP : {}", dbIp);
        log.info("DB PRIVATE IP : {}", PrivateIpDb);
        log.info("WEB IP : {}", webIp);
        log.info("SERVICE IP : {}", serviceIp);

        List<String> appIds = appDTO.stream()
                .map(AppDTO::getApplicationId)
                .collect(Collectors.toList());


        log.info("application ids : {}", appIds);

        // get application_id from parameter
        List<AppEntity> appIdList = appRepository.findByIdList(appDTO.stream().map(a -> a.getApplicationId()).toList());

        // make vpn or gslb activation ansible command
        String[] activationCommand;
        if(!acrossType){
            activationCommand = new String[] {PlaybookPath.ACTIVATION_ACROSS_SERVICE_VPN, dbIp, webIp, PrivateIpDb};
        }else {
            activationCommand = new String[] {PlaybookPath.ACTIVATION_ACROSS_SERVICE_GSLB, serviceIp};
        }

        // make vpn or gslb deactivation ansible command
        String[] deactivationCommand;
        if(!acrossType){
            deactivationCommand = new String[] {PlaybookPath.DEACTIVATION_ACROSS_SERVICE_VPN, dbIp, webIp};
        } else {
            deactivationCommand = new String[] {PlaybookPath.DEACTIVATION_ACROSS_SERVICE_GSLB, serviceIp};
        }

        log.info("activation-command : {}", Arrays.toString(activationCommand));
        log.info("deactivation-command : {}", Arrays.toString(deactivationCommand));

        // executing ansible command
        String output;
        if (!checkActivation) {
            output = commandExecutor.executor(deactivationCommand);
        } else {
            output = commandExecutor.executor(activationCommand);
        }

        // parsing ansible result
        output = output.replace(" ", "");
        String firstOutput = output.substring(0, output.lastIndexOf("\"custom_stats") - 3);
        String secondOutput = output.substring(output.lastIndexOf("\"custom_stats") - 2);
        List<String> stringList = List.of(firstOutput, secondOutput);
        List<ResultDTO> list = new ArrayList<>();

        for (String stats : stringList) {
            for (AppDTO app : appDTO) {
                log.info("ip : {} , boolean : {}", "ubuntu@"+app.getVmInstancePublicIp(), stats.contains(app.getVmInstancePublicIp()));
                if (stats.contains("ubuntu@"+app.getVmInstancePublicIp())) {
                    ResultDTO resultDTO = commandExecutor.parseResultApp("ubuntu@"+app.getVmInstancePublicIp(), stats);
                    list.add(resultDTO);
                    break;
                }
            }
        }

        log.info(list.toString());
        List<AppEntity> savedAppEntity = new ArrayList<>();

        // exception handler for failed ansible executing
        for (ResultDTO resultDTO : list) {
            if (resultDTO.getFailures() != 0){
                throw new RuntimeException("Activation Application Failed");
            } else if(resultDTO.getUnreachable() != 0) {
                throw new RuntimeException("Cannot access to remote instance");
            }
        }

        // update db and return update-result
        for (AppEntity appEntity : appIdList) {
            if (!checkActivation) {
                appEntity.setApplicationActivateYN("N");
            } else {
                appEntity.setApplicationActivateYN("Y");
            }
            savedAppEntity.add(appRepository.save(appEntity));
        }
        return AppDTO.listOf(savedAppEntity);
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for application health-checking
    *
    **/
    public AppDTO healthChecking(String serviceInstanceId, String appId) {

        // checking service_instance_id is existing in tb_service_instance
        InstanceEntity instance = instanceRepository.findById(serviceInstanceId)
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + serviceInstanceId));
        // checking application_id is existing in tb_application
        AppEntity appEntity = appRepository.findById(appId)
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + appId));

        // get public ip and application type
        String ip = "ubuntu@"+instance.getPublicIp();
        String appType = appEntity.getApplicationType();

        var inputAppDto = new AppDTO();
        inputAppDto.setVmInstancePublicIp(ip);
        ResultDTO resultDTO = new ResultDTO();

        // executing ansible
        String output;
        if (appType.equals("WEB")) {
            output = commandExecutor.executePlaybook(ip, PlaybookPath.HEALTH_CHECKING_WEB);
        } else if (appType.equals("DB")){
            output = commandExecutor.executePlaybook(ip, PlaybookPath.HEALTH_CHECKING_DB);
        } else {
            output = commandExecutor.executePlaybook(ip, PlaybookPath.HEALTH_CHECKING_SERVICE);
        }

        // parsing ansible result
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("ansible-result : {}", resultDTO);

        // database update by ansible result
        AppEntity savedAppEntity;
        log.info(String.valueOf(resultDTO));
        if (resultDTO.getFailures() != 0 || resultDTO.getUnreachable() != 0) {
            if (appType.equals("WEB")) {
                appEntity.setApplicationActivateYN("N");
            } else {
                appEntity.setApplicationActivateYN("N");
            }
        } else {
            if ("Y".equals(appEntity.getApplicationActivateYN())) {
                return AppDTO.of(appEntity);
            } else {
                appEntity.setApplicationActivateYN("Y");
            }
        }
        // save and return database
        savedAppEntity = appRepository.save(appEntity);
        return AppDTO.of(savedAppEntity);
    }
}



