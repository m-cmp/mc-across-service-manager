package com.mcmp.multiCloud.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcmp.multiCloud.entity.InstanceEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * InstanceDTO 서비스 DTO 클래스
 * 
 * @details InstanceDTO 인스턴스에 대한 정보들을 담는 클래스
 * @author 박성준
 *
 */

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(name = "Instance", description = "Instance data")
public class InstanceDTO {
	@JsonProperty("service_instance_id")
	@Schema(description = "service instance id")
	private String serviceInstanceId;
	
	@JsonProperty("service_id")
	@Schema(description = "service id")
	private String serviceId;
	
	@JsonProperty("across_service_id")
	@Schema(description = "across service id")
	private Integer acrossServiceId;
	
	@Schema(description = "cloud service provider")
	private String csp;
	
	@JsonProperty("tf_path")
	@Schema(description = "tf path")
	private Map<String, Object> tfPath;
	
	@JsonProperty("tfstate_file_path")
	@Schema(description = "tfstate file path")
	private String tfstateFilePath;
		

	@JsonProperty("vm_instance_id")
	@Schema(description = "vm instance id")
	private String vmInstanceId;

	@JsonProperty("vm_instance_name")
	@Schema(description = "vm instance name")
	private String vmInstanceName;

	@JsonProperty("vm_instance_status")
	@Schema(description = "vm instance status")
	private String vmInstanceStatus;

	@JsonProperty("vm_instance_public_ip")
	@Schema(description = "vm instance public ip")
	private String vmInstancePublicIp;

	@JsonProperty("vm_instance_private_ip")
	@Schema(description = "vm instance private ip")
	private String vmInstancePrivateIp;

	@JsonProperty("vm_instance_create_date")
	@Schema(description = "vm instance create date")
	private LocalDateTime vmCreateDate;

	@JsonProperty("vm_memory_type")
	@Schema(description = "vm memory type")
	private String vmMemoryType;

	@JsonProperty("agent_activate_yn")
	@Schema(description = "agent activate yn")
	private String agentActivateYn;

	@JsonProperty("agent_deploy_yn")
	private String agentDeployYn;

	@JsonProperty("agent_deploy_date")
	@Schema(description = "agent deploy date")
	private LocalDateTime agentDeployDate;

	@JsonProperty("gcp_healthcheck_flag")
	@Schema(description = "gcp project id for health check")
	private String gcpHealthcheckFlag;
	
	@JsonProperty("vpc_id")
	@Schema(description = "vpc id")
	private Long vpcId;
	
	public static InstanceDTO of(InstanceEntity instanceEntity) {
		return InstanceDTO.builder()
				.serviceInstanceId(instanceEntity.getServiceInstanceId())
				.acrossServiceId(instanceEntity.getAcrossServiceId())
				.serviceId(instanceEntity.getServiceId())
				.csp(instanceEntity.getCsp())
				.vmInstanceId(instanceEntity.getVmInstanceId())
				.vmInstanceName(instanceEntity.getVmInstanceName())
				.vmInstanceStatus(instanceEntity.getVmInstanceStatus())
				.vmInstancePublicIp(instanceEntity.getVmInstancePublicIp())
				.vmInstancePrivateIp(instanceEntity.getVmInstancePrivateIp())
				.vmCreateDate(instanceEntity.getVmCreateDate())
				.vmMemoryType(instanceEntity.getVmMemoryType())
				.agentActivateYn(instanceEntity.getAgentActivateYn())
				.agentDeployYn(instanceEntity.getAgentDeployYn())
				.agentDeployDate(instanceEntity.getAgentDeployDate())
				.tfstateFilePath(instanceEntity.getTfstateFilePath())
				.gcpHealthcheckFlag(instanceEntity.getGcpHealthcheckFlag())
				.vpcId(instanceEntity.getVpcId()).build();
	}
}


