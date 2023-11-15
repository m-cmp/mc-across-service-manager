package com.mcmp.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mcmp.controller.domain.MigrationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : DTO class for migration
 *
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MigrationDTO {

    @JsonAlias("migration_id")
    private String migrationId;
    @JsonAlias("db_dump_file_path")
    private String dumpFilePath;
    @JsonAlias("service_instance_id")
    private String serviceInstanceId;
    @JsonAlias("source_instance_id")
    private String sourceInstanceId;
    @JsonAlias("target_instance_id")
    private String targetInstanceId;
    private Map<String, String> result;

    public static MigrationDTO of(MigrationEntity migrationEntity){
        return MigrationDTO.builder()
                .migrationId(migrationEntity.getMigrationId())
                .dumpFilePath(migrationEntity.getDumpFilePath())
                .sourceInstanceId(migrationEntity.getSourceInstanceId())
                .targetInstanceId(migrationEntity.getTargetInstanceId())
                .build();
    }

}
