package com.mcmp.webserver.monitoring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.monitoring.service.NetResponseService;

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
@RequestMapping("/api/v1/bff/monitoring/net-response")
public class NetResponseMonitoringController {

	private final NetResponseService netResponseService;

	/**
	 * 인스턴스의 네트워크 응답 시간 추이
	 * 
	 * @param hostName
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@GetMapping("/time/transition/{instanceName}")
	@Operation(summary = "1개 인스턴스의 네트워크 응답 시간 추이", description = "1개 인스턴스의 네트워크 응답 시간 추이")
	@Schema(description = "인스턴스의 네트워크 응답 시간 추이 response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<Map<String, Object>>> getNetResponseTimeTransByHostName(
			@PathVariable("instanceName") String hostName) {
		log.debug("[NetResponseMonitoringController] getNetResponseTimeTransByHostName - 1개 인스턴스의 네트워크 응답 타입 추이");
		log.debug("hostName : {}", hostName);
		return ResponseEntity.ok().body(netResponseService.getNetResponseTimeTransByHostName(hostName));
	}
}
