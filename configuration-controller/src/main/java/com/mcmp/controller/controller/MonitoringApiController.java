package com.mcmp.controller.controller;

import com.mcmp.controller.dto.AgentActivationParamDTO;
import com.mcmp.controller.dto.InstanceDTO;
import com.mcmp.controller.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/6/23
 * @class-description : Api controller class for monitoring-service
 *
 **/
@RestController
@RequestMapping("/api/v1/controller")
@RequiredArgsConstructor
@Tag(name="monitoring-service", description="Monitoring-service API")
@Slf4j
public class MonitoringApiController {

    @Autowired
    private final MonitoringService monitoringService;
    /**
     * @date : 10/6/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to deploy and then. call deploy method
     **/
    @Operation(summary = "Monitoring-Agent Installation", description = "Installation monitoring agent")
    @Schema(description = "Monitoring-agent Installation API Response")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping("/services/collectors")
    public ResponseEntity deployAgent(@RequestBody List<InstanceDTO> instanceDTO) {
        log.info("monitoring-service deployAgent start");
        var result = monitoringService.deployAgent(instanceDTO);
        log.info("monitoring-service deployAgent finish");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * @date : 10/6/23
     * @author : Jihyeong Lee
     * @method-description : method for activating a monitoring-agent
     **/
    @Operation(summary = "Monitoring-Agent Activation", description = "Activation deployed monitoring-agent")
    @Schema(description = "Monitoring-agent Activation API Response")
    @ApiResponse(responseCode = "200", description = "Activated")
    @PutMapping("/services/collectors/activation")
    public ResponseEntity activationAgent(@RequestBody InstanceDTO instance) {
        log.info("monitoring-service activationAgent start");
        var result = monitoringService.activationAgent(instance);
        log.info("monitoring-service activationAgent finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     *
     * @date : 10/6/23
     * @author : Jihyeong Lee
     * @method-description :  method for activating multiple monitoring-agents
     *
     **/
    @Operation(summary = "Monitoring-Agent On Multiple-instance Activation", description = "Activation deployed monitoring-agent on multiple-instance")
    @Schema(description = "Monitoring-agent Activation API Response")
    @ApiResponse(responseCode = "200", description = "Activated")
    @PutMapping("/across-services/collectors/activation")
    public ResponseEntity activationAgentMultiCloud(@RequestBody List<AgentActivationParamDTO> agentActivationParam) throws IOException, InterruptedException {
        log.info("monitoring-service activationAgentMultiCloud start");
        var result = monitoringService.activationAgentMultiCloud(agentActivationParam);
        log.info("monitoring-service activationAgentMultiCloud finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
