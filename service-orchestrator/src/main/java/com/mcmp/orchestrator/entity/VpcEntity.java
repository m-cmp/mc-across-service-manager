package com.mcmp.orchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VPC 엔티티 클래스
 */
@Entity(name="tb_vpc")
@NoArgsConstructor
@Data
public class VpcEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="vpc_id", nullable=false, length=20)
	private String vpcId;
	
	@Column(name="service_instance_id", nullable=false, length=50)
	private String serviceInstanceId;
	
	@Column(name="vpc_cidr", nullable=false, length=50)
	private String vpcCidr;
	
	@Column(name="subnet_cidr", nullable=false, length=50)
	private String subnetCidr;
	
	@Column(name="vpc_create_date", nullable=false)
	private String vpcCreateDate;
	
	@Column(name="vpn_tunnel_ip", nullable=true, length=15)
	private String vpnTunnelIp;
	
	@Column(name="vpn_tunnel_create_date", nullable=true)
	private String vpnTunnelCreateDate;
	
	@Column(name="tfstate_file_path", nullable=true, length=100)
	private String tfstateFilePath;
	
	@Column(name="csp", nullable=false, length=20)
	private String csp;
}
