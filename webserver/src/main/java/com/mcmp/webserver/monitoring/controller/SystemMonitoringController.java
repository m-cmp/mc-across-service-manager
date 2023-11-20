package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.systemService;

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
@RequestMapping("/api/v1/bff/monitoring/system")
public class SystemMonitoringController {

	private final systemService systemService;

	/**
	 * 1개 인스턴스의 Uptime 시간 조회
	 * 
	 * @param hostName
	 * @return
	 */
	@GetMapping("/uptime/{instanceName}")
	@Operation(summary = "인스턴스의 Uptime 시간", description = "인스턴스의 Uptime 시간")
	@Schema(description = "인스턴스의 Uptime 시간 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getUptimeByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[SystemMonitoringController] getUptimeByHostName - 연계서비스의 ping 모니터링 정보 조회");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(systemService.getUptimeByHostName(hostName));
	}
}
