package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.NetworkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Monitoring")
@RequestMapping("/api/v1/bff/monitoring/network")
public class NetworkMonitoringController {

	private final NetworkService netService;

	/**
	 * 1개 인스턴스의 네트워크 인터페이스별 패킷 추이
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/packet/transition/{instanceName}")
	@Operation(summary = "1개 인스턴스의 네트워크 인터페이스별 패킷 추이", description = "1개 인스턴스의 네트워크 인터페이스별 패킷 추이")
	@Schema(description = "1개 인스턴스의 네트워크 인터페이스별 패킷 추이 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNetPacketTransitionByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NetworkMonitoringController] getNetPacketTransitionByHostName - 1개 인스턴스의 네트워크 인터페이스별 패킷 추이");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(netService.getNetPacketTransitionByHostName(hostName));
	}
	
	/**
	 * 1개 인스턴스의 TCP & UDP 추이
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/tcpudp/transition/{instanceName}")
	@Operation(summary = "인스턴스의 TCP & UDP 추이 추이", description = "인스턴스의 TCP & UDP 추이 추이")
	@Schema(description = "인스턴스의 TCP & UDP 추이 추이 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getTCPUDPTransByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NetworkMonitoringController] getTCPUDPTransByHostName - 인스턴스의 TCP & UDP 추이 추이");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(netService.getTCPUDPTransByHostName(hostName));
	}

}
