package com.mcmp.controller.controller;

import com.mcmp.controller.dto.AppDTO;
import com.mcmp.controller.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/9/23
 * @class-description : ApiController class for deploying, de/activation and status checking application-service on an instance.
 *
 **/
@RestController
@RequestMapping("/api/v1/controller")
@RequiredArgsConstructor
@Tag(name="single-service", description="service API")
@Slf4j
public class ServiceApiController {

    @Autowired
    private final AppService appService;

    /**
     *
     * @date : 10/10/23
     * @author : Jihyeong Lee
     * @method-description : method for deployment web and db on an instance
     *
     **/
    @Operation(summary = "Service Installation", description = "Service Installation on an instance")
    @Schema(description = "Service Installation API Response")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping("/services/{service_id}/applications")
    public ResponseEntity deployApp(@Valid @RequestBody AppDTO appList) {
        log.info("service deploy-application start");
//        log.info(appList.getVmInstancePublicIp());
        var result = appService.deployApp(appList);
        log.info("service deploy-application finish");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     *
     * @date : 10/11/23
     * @author : Jihyeong Lee
     * @method-description : method for activation installed web and db
     *
     **/
    @Operation(summary = "Service Activation", description = "Service Activation")
    @Schema(description = "Service Activation API Response")
    @ApiResponse(responseCode = "200", description = "Activated")
    @PutMapping("/services/{service_id}/applications/activation")
    public ResponseEntity activationApp(@RequestBody AppDTO appDTO) {
        log.info("service activation-application start");
        var result = appService.activation(appDTO);
        log.info("service activation-application finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *
     * @date : 10/26/23
     * @author : Jihyeong Lee
     * @method-description :  method for status checking
     *
     **/
    @Operation(summary = "Service status checking", description = "Service status checking")
    @Schema(description = "Service status checking API Response")
    @ApiResponse(responseCode = "200", description = "Succeed")
    @GetMapping("/services/{serviceId}/applications")
    public ResponseEntity healthChecking(@PathVariable String serviceId, @RequestParam("service_instance_id") String serviceInstanceId, @RequestParam("application_id") String appId) {
        log.info("service application health check start");
        AppDTO result = appService.healthChecking(serviceInstanceId, appId);
        result.setServiceId(serviceId);
        log.info("service application health check finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
