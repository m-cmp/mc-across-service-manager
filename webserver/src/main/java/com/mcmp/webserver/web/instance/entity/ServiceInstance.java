package com.mcmp.webserver.web.instance.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인스턴스 Entity
 */
@Entity
@Table(name = "tb_service_instance")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceInstance {

	@Id
	@Column(name = "service_instance_id", nullable = false, length = 50)
	private String serviceInstanceId;

	@Column(name = "across_service_id", nullable = false, length = 20)
	private Long acrossServiceId;

	@Column(name = "service_id", nullable = false, length = 20)
	private Long serviceId;

	@Column(name = "vpc_id", nullable = false, length = 20)
	private Long vpcId;

	@Column(name = "csp", nullable = false, length = 5)
	private String csp;

	@Column(name = "vm_instance_id", length = 50)
	private String vmInstanceId;

	@Column(name = "vm_instance_name", length = 100)
	private String vmInstanceName;

	@Column(name = "vm_instance_status", length = 50)
	private String vmInstanceStatus;

	@Column(name = "vm_instance_public_ip", length = 15)
	private String vmInstancePublicIp;

	@Column(name = "vm_instance_private_ip", length = 15)
	private String vmInstancePrivateIp;

	@Column(name = "vm_instance_create_date", nullable = false)
	private LocalDateTime vmInstanceCreateDate;

	@Column(name = "vm_memory_type", length = 50)
	private String vmMemoryType;

	@Column(name = "agent_activate_yn", length = 10)
	private String agentActivateYn;

	@Column(name = "agent_deploy_yn", length = 1)
	private String agentDeployYn;

	@Column(name = "agent_deploy_date", nullable = false)
	private LocalDateTime agentDeployDate;

	@Column(name = "tfstate_file_path", nullable = false, length = 100)
	private String tfstateFilePath;

	@Column(name = "gcp_healthcheck_flag", length = 50)
	private String gcpHealthcheckFlag;

	@Builder
	public ServiceInstance(String serviceInstanceId, Long acrossServiceId, Long serviceId, Long vpcId, String csp,
			String vmInstanceId, String vmInstanceName, String vmInstanceStatus, String vmInstancePublicIp,
			String vmInstancePrivateIp, LocalDateTime vmInstanceCreateDate, String vmMemoryType, String agentActivateYn,
			String agentDeployYn, LocalDateTime agentDeployDate, String tfstateFilePath, String gcpHealthcheckFlag) {
		this.serviceInstanceId = serviceInstanceId;
		this.acrossServiceId = acrossServiceId;
		this.serviceId = serviceId;
		this.vpcId = vpcId;
		this.csp = csp;
		this.vmInstanceId = vmInstanceId;
		this.vmInstanceName = vmInstanceName;
		this.vmInstanceStatus = vmInstanceStatus;
		this.vmInstancePublicIp = vmInstancePublicIp;
		this.vmInstancePrivateIp = vmInstancePrivateIp;
		this.vmInstanceCreateDate = vmInstanceCreateDate;
		this.vmMemoryType = vmMemoryType;
		this.agentActivateYn = agentActivateYn;
		this.agentDeployYn = agentDeployYn;
		this.agentDeployDate = agentDeployDate;
		this.tfstateFilePath = tfstateFilePath;
		this.gcpHealthcheckFlag = gcpHealthcheckFlag;
	}
}
