package com.mcmp.webserver.web.vpc.dto;

import java.time.LocalDateTime;

import com.mcmp.webserver.web.vpc.entity.VPC;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Schema(name = "VPCDTO", description = "VPC DTO")
public class VPCDTO {

	@Schema(description = "vpc Id")
	private Long vpcId;

	@Schema(description = "service_instance_id")
	private String serviceInstanceId;
	
	@Schema(description = "csp")
	private String csp;

	@Schema(description = "vpc_cidr")
	private String vpcCidr;

	@Schema(description = "subnet_cidr")
	private String subnetCidr;

	@Schema(description = "vpc_create_date")
	private LocalDateTime vpcCreateDate;

	@Schema(description = "vpn_tunnel_ip")
	private String vpnTunnelIp;

	@Schema(description = "vpn_tunnel_create_date")
	private LocalDateTime vpnTunnelCreateDate;

	// Entity to DTO
	public static VPCDTO of(VPC vpc) {
		return VPCDTO.builder()
				.vpcId(vpc.getVpcId())
				.serviceInstanceId(vpc.getServiceInstanceId())
				.subnetCidr(vpc.getSubnetCidr())
				.csp(vpc.getCsp())
				.vpcCidr(vpc.getVpcCidr())
				.vpcCreateDate(vpc.getVpcCreateDate())
				.vpnTunnelIp(vpc.getVpnTunnelIp())
				.vpnTunnelCreateDate(vpc.getVpnTunnelCreateDate())
				.build();
	}
}
