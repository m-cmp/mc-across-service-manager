package com.mcmp.multiCloud.multi.controller;

import java.io.IOException;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.multiCloud.common.ApiCommonResponse;
import com.mcmp.multiCloud.dto.InstanceDTO;
import com.mcmp.multiCloud.multi.service.MultiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Multi-Cloud 멀티 서비스 컨트롤러 클래스
 * 
 * @details Multi-Cloud 멀티 서비스 컨트롤러 클래스
 * @author 박성준
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/multi-cloud")
public class MultiController {
	
	private final MultiService multiService;
	private final HttpServletRequest request;
	/**
	 * 멀티 서비스 인스턴스 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VM Instance Create API", description = "Copy tf file and Create VM Instance")
	@Schema(description = "Multi Service Instance Create API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/across-services")
	public ResponseEntity<ApiCommonResponse<Map<String, String>>> createInstance(@RequestBody InstanceDTO dto) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[MultiController[createInstance]] InstanceDTO : {}", dto);
		ApiCommonResponse<Map<String, String>> response = ApiCommonResponse.<Map<String, String>>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("멀티 서비스 인스턴스 생성 성공")
				.data(multiService.createInstance(dto))
				.build();
		// 생성한 vm 인스턴스의 service_instance_id, public_ip, vm_instance_id return
		return ResponseEntity.ok().body(response);


	}
	
	/**
	 * 멀티 서비스 VPC 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VPC Create API", description = "Create directory with 'service instance id', copy tf file and Create VPC")
	@Schema(description = "Multi Service VPC Create API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/across-services/vpc")
	public ResponseEntity<ApiCommonResponse<Map<String, String>>> createVpc(@RequestBody InstanceDTO dto) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[MultiController[createVpc]] InstanceDTO : {}", dto);
		ApiCommonResponse<Map<String, String>> response = ApiCommonResponse.<Map<String, String>>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("멀티 서비스 VPC 생성 성공")
				.data(multiService.createVpc(dto))
				.build();
		// 생성한 vm 인스턴스의 sevice_instance_id, tunnel_ip return
		return ResponseEntity.ok().body(response);


	}
	
	/**
	 * 멀티 서비스 VPN Tunnel 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VPN Tunnel Create API", description = "copy tf file and Create VPN Tunnel")
	@Schema(description = "Multi Service VPN Tunnel Create API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/across-services/vpn")
	public ResponseEntity<ApiCommonResponse<Map<String, String>>> createVpn(@RequestBody InstanceDTO dto, @RequestParam("customer_tunnel_ip") String customerTunnelIp, @RequestParam("customer_vpc_cidr") String customerVpcCidr) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[MultiController[createVpn]] InstanceDTO : {},  customerTunnelIp : {}, customerVpcCidr : {}", dto, customerTunnelIp, customerVpcCidr);
		ApiCommonResponse<Map<String, String>> response = ApiCommonResponse.<Map<String, String>>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("멀티 서비스 VPN 생성 성공")
				.data(multiService.createVpn(dto, customerTunnelIp, customerVpcCidr))
				.build();
		// 생성한 vm 인스턴스의 sevice_instance_id, public_ip return
		return ResponseEntity.ok().body(response);


	}
	
	/**
	 * 멀티 서비스 GSLB 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "GSLB Create API", description = "copy tf file and Create GSLB")
	@Schema(description = "Multi Service GSLB Create API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/across-services/gslb")
	public ResponseEntity<ApiCommonResponse<Map<String, String>>> createGslb(@RequestBody InstanceDTO dto, @RequestParam("public_ip") String publicIp, @RequestParam("customer_public_ip") String customerPublicIp) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[MultiController[createGslb]] InstanceDTO : {},  publicIp : {}, customerPublicIp : {}", dto, publicIp, customerPublicIp);
		ApiCommonResponse<Map<String, String>> response = ApiCommonResponse.<Map<String, String>>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("멀티 서비스 GSLB 생성 성공")
				.data(multiService.createGslb(dto, publicIp, customerPublicIp))
				.build();
		// 생성한 vm 인스턴스의 sevice_instance_id, public_ip, domain return
		return ResponseEntity.ok().body(response);


	}
	
	/**
	 * 멀티 서비스 GSLB 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "Delete API", description = "Delete created VM Instances and directory OR Delete only created VM Instances")
	@Schema(description = "Multi Service Delete API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/across-services")
	public ResponseEntity<ApiCommonResponse<String>> deleteInstace(@RequestBody InstanceDTO dto, @RequestParam("vmOnlyYn") String vmOnlyYn) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[MultiController[deleteInstance]] InstanceDTO : {}", dto);
		ApiCommonResponse<String> response = ApiCommonResponse.<String>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("멀티 서비스 삭제 성공")
				.data(multiService.deleteInstance(dto, vmOnlyYn))
				.build();
		// 생성한 vm 인스턴스의 sevice_instance_id, public_ip, domain return
		return ResponseEntity.ok().body(response);


	}
}
