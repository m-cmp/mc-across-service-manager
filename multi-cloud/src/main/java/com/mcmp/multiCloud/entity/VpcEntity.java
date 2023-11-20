package com.mcmp.multiCloud.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *	VPC 서비스 Entity
 */
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "tb_vpc")
public class VpcEntity {

	@Id
	@Column(name = "vpc_id", nullable = false, length = 20 )
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long vpcId;

	@Column(name = "service_instance_id", nullable = false, length = 50 )
	private String serviceInstanceId;
	
	@Column(name = "vpc_cidr", nullable = true, length = 50 )
	private String vpcCidr;
	
	@Column(name = "subnet_cidr", nullable = true, length = 50 )
	private String subnetCidr;
	
	@Column(name = "vpc_create_date", nullable = false )
	private LocalDateTime vpcCreateDate;
	
	@Column(name = "vpn_tunnel_ip", nullable = true, length = 11 )
	private String vpnTunnelIp;
	
	@Column(name = "vpn_tunnel_create_date", nullable = true )
	private LocalDateTime vpnTunnelCreateDate;
	
	@Column(name = "tfstate_file_path", nullable = true, length = 100 )
	private String tfstateFilePath;
	
	@Column(name = "csp", nullable = false, length = 20 )
	private String csp;

	@Builder
	public VpcEntity(Long vpcId, String serviceInstanceId, String vpcCidr, String subnetCidr,
			LocalDateTime vpcCreateDate, String vpnTunnelIp, LocalDateTime vpnTunnelCreateDate, String tfstateFilePath,
			String csp) {
		this.vpcId = vpcId;
		this.serviceInstanceId = serviceInstanceId;
		this.vpcCidr = vpcCidr;
		this.subnetCidr = subnetCidr;
		this.vpcCreateDate = vpcCreateDate;
		this.vpnTunnelIp = vpnTunnelIp;
		this.vpnTunnelCreateDate = vpnTunnelCreateDate;
		this.tfstateFilePath = tfstateFilePath;
		this.csp = csp;
	}
}
