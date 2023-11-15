package com.mcmp.webserver.web.instance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.mcmp.webserver.web.instance.entity.ServiceInstance;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "Instance", description = "서비스 인스턴스 정보")
public class ServiceInstanceDTO {

	@Schema(description = "서비스 인스턴스 id")
	private String serviceInstanceId;

	@Schema(description = "연계 서비스 id")
	private Long acrossServiceId;

	@Schema(description = "서비스 id")
	private Long serviceId;

	@Schema(description = "vpc id")
	private Long vpcId;

	@Schema(description = "클라우드 서비스 프로바이더 타입")
	private String csp;

	@Schema(description = "vm 인스턴스 id")
	private String vmInstanceId;

	@Schema(description = "vm 인스턴스명")
	private String vmInstanceName;

	@Schema(description = "vm 메모리 타입")
	private String vmMemoryType;

	@Schema(description = "vm 인스턴스 상태(health)")
	private String vmInstanceStatus;

	@Schema(description = "vm 인스턴스 public ip")
	private String vmInstancePublicIp;

	@Schema(description = "vm 인스턴스 private ip")
	private String vmInstancePrivateIp;

	@Schema(description = "vm 인스턴스 생성일시")
	private LocalDateTime vmInstanceCreateDate;

	@Schema(description = "agent 활성화 여부(Y/N)")
	private String agentActivateYn;

	@Schema(description = "agent 배포 여부(Y/N)")
	private String agentDeployYn;

	@Schema(description = "agent 배포 일시")
	private LocalDateTime agentDeployDate;

	@Schema(description = "서비스 템플릿 파일 경로")
	private String tfstateFilePath;

	@Schema(description = "GCP project id")
	private String gcpHealthcheckFlag;

	public ServiceInstance toEntity() {
		return ServiceInstance.builder().serviceInstanceId(serviceInstanceId).acrossServiceId(acrossServiceId)
				.serviceId(serviceId).vpcId(vpcId).csp(csp).vmInstanceId(vmInstanceId).vmInstanceName(vmInstanceName)
				.vmMemoryType(vmMemoryType).vmInstanceStatus(vmInstanceStatus).vmInstancePublicIp(vmInstancePublicIp)
				.vmInstancePrivateIp(vmInstancePrivateIp).vmInstanceCreateDate(vmInstanceCreateDate)
				.agentActivateYn(agentActivateYn).agentDeployYn(agentDeployYn).agentDeployDate(agentDeployDate)
				.tfstateFilePath(tfstateFilePath).gcpHealthcheckFlag(gcpHealthcheckFlag).build();
	}

	public static ServiceInstanceDTO of(ServiceInstance instance) {
		return ServiceInstanceDTO.builder().serviceInstanceId(instance.getServiceInstanceId())
				.acrossServiceId(instance.getAcrossServiceId()).serviceId(instance.getServiceId())
				.vpcId(instance.getVpcId()).csp(instance.getCsp()).vmInstanceId(instance.getVmInstanceId())
				.vmInstanceName(instance.getVmInstanceName()).vmMemoryType(instance.getVmMemoryType())
				.vmInstanceStatus(instance.getVmInstanceStatus()).vmInstancePublicIp(instance.getVmInstancePublicIp())
				.vmInstancePrivateIp(instance.getVmInstancePrivateIp())
				.vmInstanceCreateDate(instance.getVmInstanceCreateDate()).agentActivateYn(instance.getAgentActivateYn())
				.agentDeployYn(instance.getAgentDeployYn()).agentDeployDate(instance.getAgentDeployDate())
				.tfstateFilePath(instance.getTfstateFilePath()).gcpHealthcheckFlag(instance.getGcpHealthcheckFlag())
				.build();
	}

}
