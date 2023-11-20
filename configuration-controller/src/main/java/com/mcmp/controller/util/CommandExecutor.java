package com.mcmp.controller.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmp.controller.dto.InstanceDTO;
import com.mcmp.controller.dto.ResultDTO;
import com.mcmp.controller.repository.AppRepository;
import com.mcmp.controller.repository.InstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


@Service
@Component
@Slf4j
public class CommandExecutor {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private CommonUtil commonUtil;

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for executing ansible-playbook
    *
    **/
    public String executePlaybook(String ip, String playbookPath) {
        try {
            String[] command = {"ansible-playbook", "-i", ip + ",", playbookPath};
            log.info("playbookPath : {}", playbookPath);
            log.info(ip);
            log.info(Arrays.toString(command));
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = stdoutReader.readLine()) != null) {
                output.append(line);
                log.info(line);
                output.append(System.lineSeparator());
            }

            // Read the error of the command
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder errorOutput = new StringBuilder();

            while ((line = stderrReader.readLine()) != null) {
                errorOutput.append(line);
                log.error(line);  // Print to console for debugging
                errorOutput.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            log.info(String.valueOf(exitCode));
            log.error(String.valueOf(errorOutput));

            return output.toString();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing ansible-playbook command.", e);
        }
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for executing ansible-playbook with variable
    *
    **/
    public String executeAnsiblePlaybook(String ip, String playbookPath, String variable) {
        try {
            // Prepare the command
            String[] command = {"ansible-playbook", "-i", ip + ",", playbookPath, "-e", variable};
            log.info("playbookPath : {}", playbookPath);

            // Create ProcessBuilder instance and start the process
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
                System.out.println(line);
                log.info(line);  // Print to console for debugging
                output.append(System.lineSeparator());
            }

            int exitCode = process.waitFor();

            log.info("processing-exitCode : {}", exitCode);


            return output.toString();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing ansible-playbook command.", e);
        }
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for executing linux command
    *
    **/
    public String executor(String[] command) {
        try {
            log.info(Arrays.toString(command));
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                log.info(line);
                output.append(System.lineSeparator());
            }

            process.waitFor();

            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing command: " + Arrays.toString(command), e);
        }
    }



    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for extracting ansible result
    *
    **/
    public ResultDTO parseResult(String ip, ResultDTO resultDTO, String jsonOutput) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonOutput);

        JsonNode statsNode = rootNode.path("stats");
        JsonNode ipStatsNode = statsNode.path(ip);

        if (!ipStatsNode.isMissingNode()) {
            resultDTO.setChanged(ipStatsNode.get("changed").asInt());
            resultDTO.setFailures(ipStatsNode.get("failures").asInt());
            resultDTO.setOk(ipStatsNode.get("ok").asInt());
            resultDTO.setUnreachable(ipStatsNode.get("unreachable").asInt());
        } else {
            throw new IllegalArgumentException("Invalid or incomplete Ansible playbook result.");
        }

        log.info("ansible-result : {}", resultDTO);

        return resultDTO;
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for extracting ansible result
    *
    **/
    public ResultDTO parseResultApp(String ip, String jsonOutput) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonOutput);

        JsonNode statsNode = rootNode.path("stats");
        JsonNode ipStatsNode = statsNode.path(ip);
        ResultDTO resultDTO = new ResultDTO();
        if (!ipStatsNode.isMissingNode()) {
            resultDTO.setChanged(ipStatsNode.get("changed").asInt());
            resultDTO.setFailures(ipStatsNode.get("failures").asInt());
            resultDTO.setOk(ipStatsNode.get("ok").asInt());
            resultDTO.setUnreachable(ipStatsNode.get("unreachable").asInt());
        } else {
            throw new IllegalArgumentException("Invalid or incomplete Ansible playbook result.");
        }

        log.info("ansible-result : {}", resultDTO);

        return resultDTO;
    }

    /**
    *
    * @date : 10/31/23
    * @author : Jihyeong Lee
    * @method-description : method for extracting backup-dir from ansible result
    *
    **/
    public String parseResultMigration(String ip, String jsonOutput) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonOutput);

        String backupFilePath = root.path("plays").get(0).path("tasks").get(8).path("hosts").path(ip).path("msg").asText();
        log.info("backup-file : {}", backupFilePath);
        return backupFilePath;
    }

}

