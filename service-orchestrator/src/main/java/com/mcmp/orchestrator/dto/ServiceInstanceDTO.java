package com.mcmp.orchestrator.dto;

import java.util.List;

//import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcmp.orchestrator.entity.ServiceInstanceEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 서비스 인스턴스 DTO 클래스
 * 
 * @details ServiceDTO 서비스 인스턴스에 대한 정보들을 담는 클래스
 * @author 오승재
 *
 */
@Schema(name="Service instance", description="Service instance data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ServiceInstanceDTO {
	
	@JsonProperty("service_id")
	@Schema(description = "service id")
	private String serviceId;
	
	@JsonProperty("service_instance_id")
	@Schema(description = "service instance id")
	private String serviceInstanceId;
	
	@JsonProperty("across_service_id")
	@Schema(description = "across service id")
	private String acrossServiceId;
	
	@Schema(description = "cloud service provider")
	private String csp;
	
	@JsonProperty("tf_path")
	@Schema(description = "tf file path")
	private TfPathDTO tfPath;
	
	@JsonProperty("vm_instance_id")
	@Schema(description = "vm instance id")
	private String vmInstanceId;
	
	@JsonProperty("vm_public_ip")
	@Schema(description = "vm instance public ip")
	private String publicIp;
	
	@JsonProperty("vm_instance_status")
	@Schema(description = "vm instance status")
	private String vmInstanceStatus;
	
	@JsonProperty("application_id")
	@Schema(description = "application id")
	private String applicationId;
	
	@JsonProperty("application_type")
	@Schema(description = "application type")
	private String applicationType;
	
//	@JsonAlias("application_activate_yn")
	@JsonProperty("application_status")
	@Schema(description = "application activate status")
	private String applicationStatus;
	
	@JsonProperty("tfstate_file_path")
	@Schema(description = "tfstate file path")
	private String tfstateFilePath;
	
	@JsonProperty("vm_only_yn")
	@Schema(description = "delete vm only yn")
	private String vmOnlyYn;
	
	@JsonProperty("agent_deploy_yn")
	@Schema(description = "agent deployed yn")
	private String agentDeployYn;
	
	public ServiceInstanceDTO(String serviceInstanceId, String vmOnlyYn) {
		this.serviceInstanceId = serviceInstanceId;
		this.vmOnlyYn = vmOnlyYn;
	}
	
	public static ServiceInstanceDTO of(ServiceInstanceEntity serviceInstanceEntity) {
		return ServiceInstanceDTO.builder()
				.serviceId(serviceInstanceEntity.getServiceId())
				.serviceInstanceId(serviceInstanceEntity.getServiceInstanceId())
				.acrossServiceId(serviceInstanceEntity.getAcrossServiceId())
				.csp(serviceInstanceEntity.getCsp())
				.vmInstanceId(serviceInstanceEntity.getVmInstanceId())
				.publicIp(serviceInstanceEntity.getVmPublicIp())
				.vmInstanceStatus(serviceInstanceEntity.getVmInstanceStatus())
//				.applicationActivateYn(serviceInstanceEntity.getApp)
				.agentDeployYn(serviceInstanceEntity.getAgentDeployYn())
				.tfstateFilePath(serviceInstanceEntity.getTfstateFilePath())
				.build();
	}
	
	public static List<ServiceInstanceDTO> listOf(List<ServiceInstanceEntity> serviceInstanceList) {
		return serviceInstanceList.stream().map(a -> ServiceInstanceDTO.of(a)).toList();
	}
}
