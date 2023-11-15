package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.MemoryService;

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
@RequestMapping("/api/v1/bff/monitoring/memory")
public class MemoryMonitoringController {

	private final MemoryService memService;

	/**
	 * 메모리 Used 모니터링 조회 API
	 * 
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/used")
	@Operation(summary = "전체 Memory Used 모니터링 조회", description = "전체 Memory Used 모니터링 조회 API")
	@Schema(description = "전체 Memory Used 모니터링 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getMemoryUsed() {
		log.debug("[MemoryMonitoringController] getMemoryUsed - 전체 Memory Used 모니터링 조회");

		return ResponseEntity.ok().body(memService.getMemoryUsed());
	}

	/**
	 * 1개 인스턴스의 메모리 사용량 모니터링 조회
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/used/{instanceName}")
	@Operation(summary = "1개 인스턴스의 Memory Used 모니터링 조회 API", description = "1개 인스턴스의 Memory Used 모니터링 조회 API")
	@Schema(description = "Memory Used 모니터링 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getMemoryUsedByInstanceId(
			@PathVariable("instanceName") String hostName) {
		log.debug("[MemoryMonitoringController] getMemoryUsedByInstanceId - 1개 인스턴스의 Memory Used 모니터링 조회");
		log.debug("hostName : {}", hostName);

		return ResponseEntity.ok().body(memService.getMemoryUsedByHostName(hostName));
	}

	/**
	 * 1개 인스턴스의 상세 메모리 사용 모니터링
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/resource/{instanceName}")
	@Operation(summary = "1개 인스턴스의 Memory 유형별 모니터링 조회", description = "1개 인스턴스의 Memory 유형별 모니터링 조회 API")
	@Schema(description = "1개 인스턴스의 Memory 유형별 모니터링 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getMemoryResourceByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[MemoryMonitoringController] getMemoryResourceByHostName - Memory 유형별 모니터링 조회");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(memService.getMemoryResourceByHostName(hostName));
	}
}
