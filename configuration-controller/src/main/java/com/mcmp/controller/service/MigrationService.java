package com.mcmp.controller.service;


import com.mcmp.controller.domain.InstanceEntity;
import com.mcmp.controller.domain.MigrationEntity;
import com.mcmp.controller.dto.MigrationDTO;
import com.mcmp.controller.dto.ResultDTO;
import com.mcmp.controller.exceptionHandler.GlobalExceptionHandler;
import com.mcmp.controller.repository.InstanceRepository;
import com.mcmp.controller.repository.MigrationRepository;
import com.mcmp.controller.util.CommandExecutor;
import com.mcmp.controller.util.PlaybookPath;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;
/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 10/31/23
* @class-description : service class for migration
*
**/
@Service
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class MigrationService {

    @Autowired
    private MigrationRepository migrationRepository;

    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private CommandExecutor commandExecutor;

    @Value("${ansible-playbook.base-dir}")
    public String baseDir;

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for back-up data and bring to local
    *
    **/
    public Object dataDump(MigrationDTO migrationDTO) {

        // checking parameter is not null
        if(migrationDTO.getServiceInstanceId().isEmpty()){
            throw new GlobalExceptionHandler.WrongParameterException("No parameter");
        }

        InstanceEntity instance = instanceRepository.findById(migrationDTO.getServiceInstanceId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + migrationDTO.getServiceInstanceId()));

        String ip = "ubuntu@"+instance.getPublicIp();

        // executing ansible
        String output = commandExecutor.executePlaybook(ip, baseDir + PlaybookPath.MIGRATION_DUMP);

        // parsing back-up File path
        String backFile = null;
        try {
            backFile = commandExecutor.parseResultMigration(ip, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(String.valueOf(resultDTO.getFailures()));

        // handling exception if ansible executing failed and update database
        MigrationEntity savedMigrationEntity;
        if (resultDTO.getFailures() != 0){
            throw new RuntimeException("Data Dump Failed");
        } else if (resultDTO.getUnreachable() != 0) {
            throw new RuntimeException("Unreached to instance");
        } else{
            MigrationEntity migrationEntity = new MigrationEntity();
            migrationEntity.setSourceInstanceId(migrationDTO.getServiceInstanceId());
            migrationEntity.setDumpFilePath(backFile);
            savedMigrationEntity = migrationRepository.save(migrationEntity);
        }
        if(savedMigrationEntity.getSourceInstanceId() == null){
            throw new NoSuchElementException("failed data upload");
        } else {
            return MigrationDTO.of(savedMigrationEntity);
        }
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for data-restore
    *
    **/
    public Object migration(MigrationDTO migrationDTO) {

        // checking parameter is null
        if(migrationDTO.getMigrationId().isEmpty() || migrationDTO.getServiceInstanceId().isEmpty() || migrationDTO.getDumpFilePath().isEmpty()){
            throw new GlobalExceptionHandler.WrongParameterException("Parameter invalid");
        }

        InstanceEntity instance = instanceRepository.findById(migrationDTO.getServiceInstanceId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + migrationDTO.getServiceInstanceId()));

        MigrationEntity migration = migrationRepository.findById(migrationDTO.getMigrationId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + migrationDTO.getMigrationId()));

        // get ip and back-up file path
        String ip = "ubuntu@"+instance.getPublicIp();
        String variable = "dir_path="+migrationDTO.getDumpFilePath();
        log.info("BackupData_dir : {}", migrationDTO.getDumpFilePath());

        // executing ansible with ip and back-up file path
        String output = commandExecutor.executeAnsiblePlaybook(ip, baseDir + PlaybookPath.MIGRATION_RESTORE, variable);

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("ansible-result : {}", resultDTO);

        // handling exception if ansible executing failed and update result on database
        MigrationEntity savedMigrationEntity;
        if(resultDTO.getFailures() != 0){
            throw new RuntimeException("Data Migration Failed");
        } else if (resultDTO.getUnreachable() != 0){
            throw new RuntimeException("Cannot access to remote instance");
        }else {
            migration.setTargetInstanceId(migrationDTO.getServiceInstanceId());
            savedMigrationEntity = migrationRepository.save(migration);
        }
        if(savedMigrationEntity.getTargetInstanceId() == null){
            throw new NoSuchElementException("failed data upload");
        } else {
            return MigrationDTO.of(savedMigrationEntity);
        }
    }
}
