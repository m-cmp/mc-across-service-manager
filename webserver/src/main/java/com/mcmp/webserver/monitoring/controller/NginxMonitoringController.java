package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.NginxService;

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
@RequestMapping("/api/v1/bff/monitoring/nginx")
public class NginxMonitoringController {

	private final NginxService nginxService;

	/**
	 * Nginx TPS 수 모니터링
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/tps/{instanceName}")
	@Operation(summary = "1개 인스턴스의 Nginx TPS 모니터링", description = "1개 인스턴스의 Nginx TPS 모니터링")
	@Schema(description = "1개 인스턴스의 Nginx TPS 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNginxTpsByHostName(
			@PathVariable("instanceName") String hostName) {
		
		log.debug("[NginxMonitoringController] getNginxTpsByHostName - 1개 인스턴스의 Nginx TPS 모니터링");
		log.debug("hostName : {}", hostName);
		
		return ResponseEntity.ok().body(nginxService.getNginxTpsByHostName(hostName));
	}
	
	/**
	 * Nginx TPS 추이 (1H)
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/tps/transition/{instanceName}")
	@Operation(summary = "Nginx TPS 추이 모니터링", description = "Nginx TPS 추이 모니터링")
	@Schema(description = "Nginx TPS 추이 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNginxTpsTransitionByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NginxMonitoringController] getNginxTpsTransitionByHostName - Nginx TPS 추이 모니터링");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(nginxService.getNginxTpsTransitionByHostName(hostName));
	}


	/**
	 * Nginx Active 수 모니터링
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/active/{instanceName}")
	@Operation(summary = "1개 인스턴스의 Nginx Active 수 모니터링", description = "1개 인스턴스의 Nginx Active 수 모니터링")
	@Schema(description = "1개 인스턴스의 Nginx Active 수 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNginxActiveByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NginxMonitoringController] getNginxActiveByHostName - 1개 인스턴스의 Nginx Active 수 모니터링");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(nginxService.getNginxActiveByHostName(hostName));
	}
	
	
	/**
	 * Nginx Active 추이
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/active/transition/{instanceName}")
	@Operation(summary = "Nginx Active 추이", description = "Nginx Active 추이")
	@Schema(description = "Nginx Active 추이")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNginxActiveTransByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NginxMonitoringController] getNginxActiveTransByHostName - Nginx Active 추이");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(nginxService.getNginxActiveTransByHostName(hostName));
	}
}
