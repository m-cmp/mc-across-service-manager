package com.mcmp.controller.controller;

import com.mcmp.controller.dto.AppDTO;
import com.mcmp.controller.service.AcrossService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
 * @date : 10/26/23
 * @class-description : across-service ApiController class for deploying, de/activation, and status checking application-service on multiple instances
 *
 **/
@RestController
@RequestMapping("/api/v1/controller")
@AllArgsConstructor
@Tag(name="Application-service", description="Application-service API")
@Slf4j
public class AcrossServiceApiController {

    @Autowired
    private AcrossService acrossService;

    /**
     *
     * @date : 10/26/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to deploy web or db and call deploying web method
     *
     **/
    @Operation(summary = "Across-service Installation", description = "Installation across-service on multi-cloud")
    @Schema(description = "Across-service Installation API Response")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping("/across-services/{across_service_id}/applications")
    public ResponseEntity deployAcrossService(@RequestBody AppDTO appDTO) {
        log.info("across-service deploy start");
        var result = acrossService.deployAcrossService(appDTO);
        log.info("across-service deploy finish");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     *
     * @date : 10/26/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to activate web and db, and then call activation method
     *
     **/
    @Operation(summary = "Across-service Activation", description = "Across-service Activation")
    @Schema(description = "Across-service Activation API Response")
    @ApiResponse(responseCode = "200", description = "Activated")
    @PutMapping("/across-services/{across_service_id}/applications/activation")
    public ResponseEntity activateApp(@RequestBody List<AppDTO> appDTO) {
        log.info("across-service activation start");
        Object result = null;
        try {
            result = acrossService.activation(appDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("across-service activation finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *
     * @date : 10/26/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to checking status of web or db, and then call healthChecking method
     *
     **/
    @Operation(summary = "Across-service status checking", description = "Across-service status checking")
    @Schema(description = "Across-service status checking API Response")
    @ApiResponse(responseCode = "200", description = "Activated")
    @GetMapping("/across-services/{acrossServiceId}/applications")
    public ResponseEntity healthChecking(@PathVariable String acrossServiceId, @RequestParam("service_instance_id") String serviceInstanceId, @RequestParam("application_id") String appId) {
        log.info("across-service health-check start");
        AppDTO result = acrossService.healthChecking(serviceInstanceId, appId);
        result.setAcrossServiceId(acrossServiceId);
        log.info("across-service health-check finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}