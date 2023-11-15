package com.mcmp.webserver.web.instance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcmp.webserver.web.instance.dto.ServiceInstanceDTO;
import com.mcmp.webserver.web.instance.service.InstanceService;
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
@Tag(name = "ServiceInstance", description = "인스턴스 API")
public class ServiceInstanceApiController {

	private final InstanceService ServiceInstanceService;

	/**
	 * 인스턴스 전체 목록 응답 API
	 * 
	 * @return ResponseEntity<List<ServiceInstanceDTO>>
	 */
	@GetMapping("/api/v1/bff/instance")
	@Operation(summary = "인스턴스 목록 조회 API", description = "인스턴스 목록 조회")
	@Schema(description = "인스턴스 목록 조회 응답")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	public ResponseEntity<List<ServiceInstanceDTO>> getServiceInstanceList() {
		log.debug("[ServiceInstanceApiController] getServiceInstanceList - 인스턴스 목록 조회");
		
		ResponseEntity<List<ServiceInstanceDTO>> response;
		List<ServiceInstanceDTO> ServiceInstanceList = ServiceInstanceService.selectAll();
		response = ResponseEntity.ok().body(ServiceInstanceList);
		return response;
	}
}
