package com.mcmp.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mcmp.controller.domain.InstanceEntity;
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
 * @date : 10/27/23
 * @class-description : DTO class for vm_instance
 *
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InstanceDTO {

    private String serviceInstanceId;
    @JsonAlias("vm_public_ip")
    private String vmInstancePublicIp;
    @JsonAlias("agentActivateYN")
    private String agentActivateYN;
    private String agentDeployYN;
    private String agentDeployDate;
    private String acrossServiceId;
    private Map<String, String> result;

    public static InstanceDTO of(InstanceEntity instanceEntity){
        return InstanceDTO.builder()
                .serviceInstanceId(instanceEntity.getServiceInstanceId())
                .vmInstancePublicIp(instanceEntity.getPublicIp())
                .agentActivateYN(instanceEntity.getAgentActivateYN())
                .agentDeployYN(instanceEntity.getAgentDeployYN())
                .agentDeployDate(instanceEntity.getAgentDeployDate())
                .acrossServiceId(instanceEntity.getAcrossServiceId())
                .build();
    }

    public static List<InstanceDTO> listOf(List<InstanceEntity> instanceEntityList) {
        return instanceEntityList.stream().map(a -> InstanceDTO.of(a)).toList();
    }
}
