package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.PingService;

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
@RequestMapping("/api/v1/bff/monitoring/ping")
public class PingMonitoringController {

	private final PingService pingService;

	/**
	 * 연계서비스의 ping 모니터링 정보 조회
	 * 
	 * @param hosts
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/across")
	@Operation(summary = "연계서비스의 ping 모니터링 정보 조회", description = "연계서비스의 ping 모니터링 정보 조회 API")
	@Schema(description = "연계서비스의 ping 모니터링 정보 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getTotalCPUUsage(@RequestParam("hosts") List<String> hosts) {
		log.debug("[PingMonitoringController] getTotalCPUUsage - 연계서비스의 ping 모니터링 정보 조회");
		log.debug("host List : {}", hosts);
		return ResponseEntity.ok().body(pingService.getPingAvgTimeByHostNames(hosts));
	}

}
