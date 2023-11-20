package com.mcmp.orchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 서비스 인스턴스 엔티티 클래스
 */
@Data
@NoArgsConstructor
@Entity(name="tb_service_instance")
public class ServiceInstanceEntity {
	
	@Id
	@Column(name="service_instance_id", nullable=false, length=100)
	private String serviceInstanceId;
	@Column(name="service_id", nullable=true, length=30)
	private String serviceId;
	@Column(name="across_service_id", nullable=true, length=30)
	private String acrossServiceId;
	@Column(name="csp", nullable=false, length=20)
	private String csp;
	@Column(name="vm_instance_id", nullable=false, length=150)
	private String vmInstanceId;
	@Column(name="vm_instance_public_ip", nullable=true, length=15)
	private String vmPublicIp;
	@Column(name="vm_instance_status", nullable=false, length=50)
	private String vmInstanceStatus;
	@Column(name="agent_deploy_yn", nullable=false, length=1)
	private String agentDeployYn;
	@Column(name="tfstate_file_path", nullable=false, length=100)
	private String tfstateFilePath;
	@Column(name="gcp_healthcheck_flag", nullable=true, length=50)
	private String gcpHealthcheckFlag;
}
