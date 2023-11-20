package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.CPUService;

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
@RequestMapping("/api/v1/bff/monitoring/cpu")
public class CPUMonitoringController {

	private final CPUService cpuService;

	/**
	 * N개 인스턴스별 cpu total Usage 모니터링 정보 조회
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/usage")
	@Operation(summary = "전체 CPU Usage 모니터링 조회", description = "전체 CPU Usage 모니터링 조회 API")
	@Schema(description = "전체 CPU Usage 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getTotalCPUUsage() {
		log.debug("[ServiceInfoApiController] getServiceInfoList - 단일 서비스 목록 조회");

		return ResponseEntity.ok().body(cpuService.getTotalCPUUsage());
	}

	/**
	 * 1개 인스턴스의 total CPU 자원 모니터링 정보 조회
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/usage/{instanceName}")
	@Operation(summary = "단일 인스턴스의 CPU Usage 모니터링 조회", description = "단일 인스턴스의 CPU Usage 모니터링 조회 API")
	@Schema(description = "단일 인스턴스의 CPU Usage 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getCPUUsageByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[CPUMonitoringController] getCPUUsageByHostName - 단일 인스턴스의 CPU Usage 모니터링 조회");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(cpuService.getCPUUsageByHostName(hostName));
	}

	/**
	 * 1개 인스턴스의 CPU 사용률 평균 모니터링 조회 (1m)
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */ 
	@GetMapping("/used/{instanceName}")
	@Operation(summary = "1개 인스턴스의 CPU 사용률 평균 모니터링 조회", description = "1개 인스턴스의 CPU 사용률 평균 모니터링 조회")
	@Schema(description = "CPU Usage 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getCPUUsedByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[CPUMonitoringController] getCPUUsedByHostName - 1개 인스턴스의 CPU 사용률 평균 모니터링 조회 (1m)");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(cpuService.getCPUUsedByHostName(hostName));
	}
}
