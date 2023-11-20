package com.mcmp.multiCloud.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *	단일 서비스 Entity
 */
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tb_service_instance")
public class InstanceEntity {

	@Id
	@Column(name = "service_instance_id", nullable = false, length = 100 )
	private String serviceInstanceId;

	@Column(name = "across_service_id", nullable = true, length = 30 )
	private Integer acrossServiceId;

	@Column(name = "service_id", nullable = true, length = 30 )
	private String serviceId;

	@Column(name = "csp", nullable = false, length = 20 )
	private String csp;

	@Column(name = "vm_instance_id", nullable = false, length = 150 )
	private String vmInstanceId;

	@Column(name = "vm_instance_name", nullable = false, length = 100 )
	private String vmInstanceName;

	@Column(name = "vm_instance_status", nullable = false, length = 50 )
	private String vmInstanceStatus;

	@Column(name = "vm_instance_public_ip", nullable = true, length = 15 )
	private String vmInstancePublicIp;

	@Column(name = "vm_instance_private_ip", nullable = true, length = 15 )
	private String vmInstancePrivateIp;

	@Column(name = "vm_instance_create_date", nullable = false)
	private LocalDateTime vmCreateDate;

	@Column(name = "vm_memory_type", nullable = true, length = 50 )
	private String vmMemoryType;

	@Column(name = "agent_activate_yn", nullable = false, length = 1 )
	private String agentActivateYn;

	@Column(name = "agent_deploy_yn", nullable = false, length = 1 )
	private String agentDeployYn;

	@Column(name = "agent_deploy_date", nullable = true )
	private LocalDateTime agentDeployDate;

	@Column(name = "tfstate_file_path", nullable = false, length = 100 )
	private String tfstateFilePath;

	@Column(name = "gcp_healthcheck_flag", nullable = true, length = 50 )
	private String gcpHealthcheckFlag;
	
	@Column(name = "vpc_id", nullable = true, length = 20 )
	private Long vpcId;
	
	@Builder
	public InstanceEntity(String serviceInstanceId, String serviceTemplateId, Integer acrossServiceId,
			String serviceId, String csp, String vmInstanceId, String vmInstanceName, String vmInstanceStatus,
			String vmInstancePublicIp, String vmInstancePrivateIp, LocalDateTime vmCreateDate, String vmMemoryType,
			String agentActivateYn, String agentDeployYn, LocalDateTime agentDeployDate, String tfstateFilePath, String gcpHealthcheckFlag, Long vpcId) {
		this.serviceInstanceId = serviceInstanceId;
		this.acrossServiceId = acrossServiceId;
		this.serviceId = serviceId;
		this.csp = csp;
		this.vmInstanceId = vmInstanceId;
		this.vmInstanceName = vmInstanceName;
		this.vmInstanceStatus = vmInstanceStatus;
		this.vmInstancePublicIp = vmInstancePublicIp;
		this.vmInstancePrivateIp = vmInstancePrivateIp;
		this.vmCreateDate = vmCreateDate;
		this.vmMemoryType = vmMemoryType;
		this.agentActivateYn = agentActivateYn;
		this.agentDeployYn = agentDeployYn;
		this.agentDeployDate = agentDeployDate;
		this.tfstateFilePath = tfstateFilePath;
		this.gcpHealthcheckFlag = gcpHealthcheckFlag;
		this.vpcId = vpcId;
	}

}
