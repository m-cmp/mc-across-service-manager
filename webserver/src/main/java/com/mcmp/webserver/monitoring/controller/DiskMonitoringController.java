package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.DiskService;

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
@RequestMapping("/api/v1/bff/monitoring/disk")
public class DiskMonitoringController {

	private final DiskService diskService;

	/**
	 * 전체 인스턴스 별 Disk Used 모니터링 조회
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/used")
	@Operation(summary = "전체 인스턴스 별 Disk Used 모니터링 조회", description = "전체 Disk Used 모니터링 조회 API")
	@Schema(description = "전체 Disk Used 모니터링 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getDiskUsed() {
		log.debug("[DiskMonitoringController] getDiskUsed - 1개 인스턴스의 상세 Disk Used 평균 모니터링 조회");
		return ResponseEntity.ok().body(diskService.getDiskUsed());
	}

	/**
	 * 1개 인스턴스의 상세 Disk Used 평균 모니터링 조회 (1m)
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/used/{instanceName}")
	@Operation(summary = "1개 인스턴스의 상세 Disk Used 평균 모니터링 조회 (1m)", description = "1개 인스턴스의 상세 Disk Used 평균 모니터링 조회 (1m) API")
	@Schema(description = "1개 인스턴스의 상세 Disk Used 평균 모니터링 조회 (1m) response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getDiskUsedByHostName(@PathVariable("instanceName") String hostName) {
		log.debug("[DiskMonitoringController] getDiskUsedByHostName - 1개 인스턴스의 상세 Disk Used 평균 모니터링 조회");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(diskService.getDiskUsedByHostName(hostName));
	}
}
