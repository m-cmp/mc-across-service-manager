package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.ProcessService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Monitoring")
@RequestMapping("/api/v1/bff/monitoring/process")
public class ProcessMonitoringController {

	private final ProcessService processService;

	/**
	 * 인스턴스 별 Process 수 추이
	 * 
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */

	@GetMapping("/transition")
	@Operation(summary = "전체 인스턴스 별 Process 수 추이", description = "전체 인스턴스 별 Process 수 추이")
	@Schema(description = "전체 인스턴스 별 Process 수 추이 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getProcessTransition() {
		log.debug("[ProcessMonitoringController] getProcessTransition - 전체 인스턴스 별 Process 수 추이");
		return ResponseEntity.ok().body(processService.getProcessTransition());
	}

	/**
	 * 시간 내 인스턴스의 평균 프로세스 or 스레드 수
	 * 
	 * @param field    "total"프로세스 / "total_threads" 스레드
	 * @param hostName 인스턴스 명 리스트
	 * @param minutes  분
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/cnt/{instanceName}")
	@Operation(summary = "1개 인스턴스의 평균 프로세스 or 스레드 수", description = "1개 인스턴스의 평균 프로세스 or 스레드 수")
	@Schema(description = "1개 인스턴스의 평균 프로세스 or 스레드 수 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getFieldCntByHostAndMunutes(
			@Valid @RequestParam String field,
			@PathVariable("instanceName") String hostName, @RequestParam("time") Integer minutes) {
		log.debug("[ProcessMonitoringController] getProcessTransition - 전체 인스턴스 별 Process 수 추이");
		log.debug("field:{} hostName:{} minutes : {} ", field, hostName, minutes);
		return ResponseEntity.ok().body(processService.getFieldCntByHostAndMunutes(field, hostName, minutes));
	}

	/**
	 * 1개 인스턴스의 프로세스 상태별 추이 조회
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/status/transition/{instanceName}")
	@Operation(summary = "1개 인스턴스의 프로세스 상태별 추이 조회", description = "1개 인스턴스의 프로세스 상태별 추이 조회")
	@Schema(description = "1개 인스턴스의 프로세스 상태별 추이 조회 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getProcessStatusCntTransition(
			@PathVariable("instanceName") String hostName) {
		log.debug("[ProcessMonitoringController] getProcessStatusCntTransition -  1개 인스턴스의 프로세스 상태별 추이 조회");
		log.debug("hostName:{}  ", hostName);
		return ResponseEntity.ok().body(processService.getProcessStatusCntTransition(hostName));
	}

	/**
	 * 인스턴스 별 Thread 수 추이
	 * 
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/thread/transition")
	@Operation(summary = "인스턴스 별 Thread 수 추이", description = "인스턴스 별 Thread 수 추이")
	@Schema(description = "인스턴스 별 Thread 수 추이 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getThreadTransition() {
		log.debug("[ProcessMonitoringController] getThreadTransition -  인스턴스 별 Thread 수 추이");
		return ResponseEntity.ok().body(processService.getThreadTransition());
	}

}
