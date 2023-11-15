package com.mcmp.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mcmp.controller.domain.AppEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 11/7/23
* @class-description : dto class for application
*
**/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppDTO {

    private String serviceId;
    private String acrossServiceId;
    private String applicationId;
    private String serviceInstanceId;
    private String applicationName;
    private String applicationType;
    @JsonAlias("application_activate_yn")
    private String applicationActivateYN;
    private String applicationCreateDate;
    @JsonAlias("vm_public_ip")
    private String vmInstancePublicIp;
    private Map<String, String> result;
    private String acrossType;
    @JsonAlias("vm_instance_private_ip")
    private String vmInstancePrivateIp;

    public static AppDTO of(AppEntity appEntity){
        return AppDTO.builder()
                .applicationId(appEntity.getApplicationId())
                .serviceInstanceId(appEntity.getServiceInstanceId())
                .applicationName(appEntity.getApplicationName())
                .applicationType(appEntity.getApplicationType())
                .applicationActivateYN(appEntity.getApplicationActivateYN())
                .applicationCreateDate(appEntity.getApplicationCreateDate())
                .build();
    }

    public static List<AppDTO> listOf(List<AppEntity> appEntityList) {
        return appEntityList.stream().map(a -> AppDTO.of(a)).toList();
    }
}
