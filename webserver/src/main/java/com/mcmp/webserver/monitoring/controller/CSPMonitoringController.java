package com.mcmp.webserver.monitoring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.dto.CSPResourceMonitoringDTO;
import com.mcmp.webserver.monitoring.dto.CSPStatusMonitoringDTO;
import com.mcmp.webserver.monitoring.service.CSPService;

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
@RequestMapping("/api/v1/bff/monitoring/csp")
public class CSPMonitoringController {

	private final CSPService cspService;

	/**
	 * 전체 CSP별 자원 Count 모니터링 정보 조회
	 * 
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/resource/cnt")
	@Operation(summary = "전체 CSP별 자원 Count 모니터링 정보 조회", description = "전체 CSP별 자원 Count 모니터링 정보 조회 API")
	@Schema(description = "전체 CSP별 자원 Count 모니터링 정보 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<CSPResourceMonitoringDTO>> selectCSPResourceCnt() {
		log.debug("[CSPMonitoringController] selectCSPResourceCnt - 전체 CSP별 자원 Count 모니터링 정보 조회");
		return ResponseEntity.ok().body(cspService.selectCSPResourceCnt());
	}

	/**
	 * 전체 CSP별 상태별 모니터링 조회
	 * 
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/status/cnt")
	@Operation(summary = "전체 CSP 자원 상태별 모니터링", description = "전체 CSP 자원 상태별 모니터링 API")
	@Schema(description = "전체 CSP 자원 상태별 모니터링 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<CSPStatusMonitoringDTO> selectCSPStatusCntAll() {
		log.debug("[CSPMonitoringController] selectCSPStatusCntAll - 전체 CSP별 자원 Count 모니터링 정보 조회");
		return ResponseEntity.ok().body(cspService.selectCSPStatusMonitoringAll());
	}
}
