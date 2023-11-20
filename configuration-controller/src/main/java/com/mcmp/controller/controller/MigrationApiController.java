package com.mcmp.controller.controller;

import com.mcmp.controller.dto.MigrationDTO;
import com.mcmp.controller.service.MigrationService;
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

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Api controller class for migration-service
 *
 **/
@RestController
@RequestMapping("/api/v1/controller")
@RequiredArgsConstructor
@Tag(name="migration-service", description="migration-service API")
@Slf4j
public class MigrationApiController {

    @Autowired
    private final MigrationService migrationService;

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to dump data and then, call dataDump method
     *
     **/
    @Operation(summary = "Data dump", description = "Data dump")
    @Schema(description = "Data dump API Response")
    @ApiResponse(responseCode = "200", description = "Succeed")
    @PutMapping("/migration/{service_id}")
    public ResponseEntity dataDump(@RequestBody MigrationDTO migrationDTO){
        log.info("migration-service dataDump start");
        var result = migrationService.dataDump(migrationDTO);
        log.info("migration-service dataDump finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description : method for getting api to migration dump-data and then, call migration method
     *
     **/
    @Operation(summary = "Migration", description = "Migration Data")
    @Schema(description = "Migration API Response")
    @ApiResponse(responseCode = "200", description = "Succeed")
    @PostMapping("/migration/{service_Id}")
    public ResponseEntity migration(@RequestBody MigrationDTO migrationDTO){
        log.info("migration-service migration start");
        var result = migrationService.migration(migrationDTO);
        log.info("migration-service migration finish");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
