package com.mcmp.controller.service;

import com.mcmp.controller.domain.AppEntity;
import com.mcmp.controller.domain.InstanceEntity;
import com.mcmp.controller.dto.AgentActivationParamDTO;
import com.mcmp.controller.dto.AppDTO;
import com.mcmp.controller.dto.InstanceDTO;
import com.mcmp.controller.dto.ResultDTO;
import com.mcmp.controller.exceptionHandler.GlobalExceptionHandler;
import com.mcmp.controller.repository.InstanceRepository;
import com.mcmp.controller.util.CommandExecutor;
import com.mcmp.controller.util.CommonUtil;
import com.mcmp.controller.util.PlaybookPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.mcmp.controller.util.JsonUtil.readResult;

@Service
@Slf4j
@Transactional
public class MonitoringService {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CommandExecutor commandExecutor;


    /**
    *
    * @date : 10/6/23
    * @author : Jihyeong Lee
    * @method-description : service method for deploying one or multiple monitoring-agents
    *
    **/
    public Object deployAgent(List<InstanceDTO> instanceList) {

        // handling null parameter
        for(InstanceDTO instanceDTO : instanceList){
            InstanceEntity instance = instanceRepository.findById(instanceDTO.getServiceInstanceId())
                    .orElseThrow(() -> new NoSuchElementException("No instance found with id in inventory"));

            if(!ObjectUtils.isEmpty(instance.getAgentDeployYN()) && "Y".equals(instance.getAgentDeployYN())){
                throw new GlobalExceptionHandler.DeployedException("The agent already deployed");
            }
        }

        StringBuffer str = new StringBuffer();
        String ip = null;
        String serviceInstanceId = null;

        var instanceDto = new InstanceDTO();

        // get public ip
        for (int i = 0; i < instanceList.size(); i++) {
            ip = instanceList.get(i).getVmInstancePublicIp();
            serviceInstanceId = instanceList.get(i).getServiceInstanceId();
            instanceDto.setServiceInstanceId(serviceInstanceId);
            str.append((instanceList.size() == 1 || i != instanceList.size() - 1)?"ubuntu@" + ip + "," : "ubuntu@" + ip);
        }
        log.info("ip :  {}", str);

        String ipAddresses = str.toString();

        // make command and execute ansible
        String[] command = {"ansible-playbook", "-i", ipAddresses, PlaybookPath.INSTALLATION_TELEGRAF};
        String output = commandExecutor.executor(command);

        var resultDTO = readResult(output, ResultDTO.class);


        // extracting result from ansible json result
        for(InstanceDTO instance : instanceList) {
            log.info(ipAddresses);
            Map<String, String> result = resultDTO.getStats().get("ubuntu@"+instance.getVmInstancePublicIp());
            instance.setResult(result);
            resultDTO.setChanged(Integer.parseInt(result.get("changed")));
            resultDTO.setFailures(Integer.parseInt(result.get("failures")));
            resultDTO.setOk(Integer.parseInt(result.get("ok")));
            resultDTO.setUnreachable(Integer.parseInt(result.get("unreachable")));
            log.info(String.valueOf(resultDTO.getUnreachable()));

			// checking instance which occurred executing failure or error
			if (resultDTO.getFailures() != 0){
               throw new RuntimeException("Failed Monitoring Deployment");
            } else if (resultDTO.getUnreachable() != 0) {
                throw new RuntimeException("failed connect to instance");
			}
		}

        // update database and return result
		List<InstanceEntity> agent;
        agent = instanceRepository.findByIdList(instanceList.stream().map(a -> a.getServiceInstanceId()).toList());
        for (InstanceEntity instance : agent) {
        instance.setAgentDeployYN("Y");
        instance.setAgentActivateYN("N");
        instance.setAgentDeployDate(commonUtil.getCurrentDate());
        instanceRepository.save(instance);
        }

		return InstanceDTO.listOf(agent);
	}

    /**
    *
    * @date : 10/6/23
    * @author : Jihyeong Lee
    * @method-description : service method for activating a monitoring-agent
    *
    **/
    public Object activationAgent(InstanceDTO instanceDTO) {

        // checking and handling parameter
        InstanceEntity instance = instanceRepository.findById(instanceDTO.getServiceInstanceId())
                .orElseThrow(() -> new NoSuchElementException("No instance found with id: " + instanceDTO.getServiceInstanceId()));
        if(instanceDTO.getAgentActivateYN().equals(instance.getAgentActivateYN())){
            String yn = instanceDTO.getAgentActivateYN();
            throw new GlobalExceptionHandler.WrongParameterException("Y".equals(yn)?"Agent already running":"No agents running");
        }

        String ip = "ubuntu@"+instance.getPublicIp();
        log.info(ip);
        log.info(instanceDTO.getAgentActivateYN());

        var instanceDto = new InstanceDTO();
        instanceDto.setVmInstancePublicIp(ip);

        // make variable ansible playbook path
        String path;
        if ("Y".equals(instanceDTO.getAgentActivateYN())){
            path = PlaybookPath.ACTIVATION_TELEGRAF_SERVICE;
        } else {
            path = PlaybookPath.DEACTIVATION_TELEGRAF_SERVICE;
        }

        String output = commandExecutor.executePlaybook(ip, path);
        log.info(output);

        // parsing ansible result
        ResultDTO resultDTO = new ResultDTO();
        try {
            resultDTO = commandExecutor.parseResult(ip, resultDTO, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(output);

        // handling ansible executing failed and update database
        InstanceEntity savedInstanceEntity;
        if (resultDTO.getFailures() != 0){
            throw new RuntimeException("Monitoring Activation Failed");
        } else if (resultDTO.getUnreachable() != 0) {
            throw new RuntimeException("Cannot connect to instance");
        } else {
            // updating result into inventory when ansible finish working successfully
            var agent = instanceRepository.findById(instanceDTO.getServiceInstanceId()).orElse(null);
            try {
                if (agent.getAgentActivateYN().equals("N") && instanceDTO.getAgentActivateYN().equals("Y")) {
                    agent.setAgentActivateYN("Y");
                } else {
                    agent.setAgentActivateYN("N");
                }
            } catch (DataAccessException dae) {
                return dae.getMessage();
            }
            savedInstanceEntity = instanceRepository.save(agent);
        }
        return InstanceDTO.of(savedInstanceEntity);
    }

    /**
     * @date : 10/6/23
     * @author : Jihyeong Lee
     * @method-description : service method for activating multiple monitoring-agents
     **/
    public List<InstanceDTO> activationAgentMultiCloud(List<AgentActivationParamDTO> agentActivationParam)
            throws IOException {

        // checking more than 2 parameters are existing
        if (agentActivationParam.size() < 2) {
            throw new IllegalArgumentException("At least two parameters need");
        }

        // checking targeturl is null or not
        for (AgentActivationParamDTO dto : agentActivationParam) {
            if (dto.getPingTargetUrl() == null || dto.getPingTargetUrl().isEmpty()) {
                throw new GlobalExceptionHandler.WrongParameterException("there are no targetUrls parameter");
            }
        }

        // check activation or deactivation, and also check across service type which is GSLB or VPN
        boolean isActivation = agentActivationParam.stream().allMatch(x -> x.getAgentActivateYN().equals("Y"));
        boolean acrossType = agentActivationParam.stream().allMatch(x -> x.getAcrossType().equals("GSLB"));

        // get playbook path
        String path = null;
        if(isActivation && !acrossType){
            path = PlaybookPath.ACTIVATION_TELEGRAF_ACROSS_SERVICE_VPN;
        } else if(isActivation && acrossType){
            path = PlaybookPath.ACTIVATION_TELEGRAF_ACROSS_SERVICE_GSLB;
        } else {
            path = PlaybookPath.DEACTIVATION_TELEGRAF_ACROSS_SERVICE;
        }

        //
        String [] command = new String[5];
        int u = 0;
        command[u++] = path;
        for (AgentActivationParamDTO param : agentActivationParam) {
            command[u++] = "ubuntu@"+param.getVmInstancePublicIp();
            command[u++] = param.getPingTargetUrl();
        }

        log.info(command.toString());

        String output = commandExecutor.executor(command);

        output = output.replace(" ", "");
        String firstOutput = output.substring(0, output.lastIndexOf("\"custom_stats") - 3);
        String secondOutput = output.substring(output.lastIndexOf("\"custom_stats") - 2);

        List<String> stringList = List.of(firstOutput, secondOutput);
        List<ResultDTO> list = new ArrayList<>();


        for (String test : stringList) {
            for (AgentActivationParamDTO param : agentActivationParam) {
                log.info("ip : {} , boolean : {}", "ubuntu@"+param.getVmInstancePublicIp(), test.contains("ubuntu@"+param.getVmInstancePublicIp()));
                if (test.contains("ubuntu@"+param.getVmInstancePublicIp())) {
                    ResultDTO resultDTO = commandExecutor.parseResultApp("ubuntu@"+param.getVmInstancePublicIp(), test);
                    list.add(resultDTO);
                    break;
                }
            }
        }


        for (ResultDTO resultDTO : list) {
            if (resultDTO.getFailures() != 0){
                throw new RuntimeException("Monitoring-Agent Activation Failed");
            } else if(resultDTO.getUnreachable() != 0) {
                throw new RuntimeException("Cannot connect to instance");
            }
        }


        String activationStr = null;
        if(isActivation){
            activationStr = "Y";
        }else {
            activationStr = "N";
        }

        List<InstanceEntity> entityList = instanceRepository
                .findByIdList(agentActivationParam.stream().map(a -> a.getServiceInstanceId()).toList());

        for (InstanceEntity instance : entityList) {
            instance.setAgentActivateYN(activationStr);
            instanceRepository.save(instance);
        }
        return InstanceDTO.listOf(entityList);
    }
}
