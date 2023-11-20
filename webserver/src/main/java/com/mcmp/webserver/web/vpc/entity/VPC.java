package com.mcmp.webserver.web.vpc.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VPC Entity
 */
@Entity
@Table(name = "tb_vpc")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VPC {

	@Id
	@Column(nullable = false, length = 20)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long vpcId;

	@Column(nullable = false, length = 32)
	private String serviceInstanceId;
	
	@Column(nullable = false, length = 5)
	private String csp;
	
	@Column(nullable = false, length = 20)
	private String vpcCidr;

	@Column(nullable = false, length = 20)	
	private String subnetCidr;

	@Column(nullable = false)
	private LocalDateTime vpcCreateDate;

	@Column(nullable = false, length = 15)
	private String vpnTunnelIp;

	@Column(nullable = false)
	private LocalDateTime vpnTunnelCreateDate;

	@Builder
	public VPC(Long vpcId, String serviceInstanceId, String csp, String vpcCidr, String subnetCidr,
			LocalDateTime vpcCreateDate, String vpnTunnelIp, LocalDateTime vpnTunnelCreateDate,
			String tfstateFilePath) {
		this.vpcId = vpcId;
		this.serviceInstanceId = serviceInstanceId;
		this.csp = csp;
		this.vpcCidr = vpcCidr;
		this.subnetCidr = subnetCidr;
		this.vpcCreateDate = vpcCreateDate;
		this.vpnTunnelIp = vpnTunnelIp;
		this.vpnTunnelCreateDate = vpnTunnelCreateDate;
	}
}
