package com.mcmp.webserver.web.across.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.web.across.dto.AcrossServiceDTO;
import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceListVO;
import com.mcmp.webserver.web.across.dto.IntegratedAcrossServiceVO;
import com.mcmp.webserver.web.across.service.AcrossServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "AcrossService", description = "연계 서비스 API")
public class AcrossServiceApiController {

	private final AcrossServiceService acrossServiceService;
	
	/**
	 * 연계 서비스 통합 목록 응답 API
	 * @return ResponseEntity<List<IntegratedAcrossServiceListVO>>
	 */
	@GetMapping("/api/v1/bff/across-service")
	@Operation(summary = "연계 서비스 목록 조회 API", description = "연계 서비스 목록 조회")
	@Schema(description = "연계 서비스 목록 조회 응답")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success")
	})
	public ResponseEntity<List<IntegratedAcrossServiceListVO>> getIntegratedAcrossServiceList() {
		log.debug("[AcrossServiceApiController] getIntegratedAcrossServiceList - 연계 서비스 목록 조회");
		
		List<IntegratedAcrossServiceListVO> integratedAcrossServiceList = acrossServiceService.selectAll();
			
		return ResponseEntity.ok().body(integratedAcrossServiceList);
	}
	
	/**
	 * 연계 서비스 전체 목록 응답 API
	 * @return ResponseEntity<List<AcrossServiceDTO>>
	 */
	@GetMapping("/api/v1/bff/across-service-only")
	@Operation(summary = "연계 서비스 목록 조회 API", description = "연계 서비스 목록 조회")
	@Schema(description = "연계 서비스 목록 조회 응답")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success")
	})
	public ResponseEntity<List<AcrossServiceDTO>> getAcrossServiceList() {
		log.debug("[AcrossServiceApiController] getAcrossServiceList - 연계 서비스 목록 조회");
		
		List<AcrossServiceDTO> acrossServiceList = acrossServiceService.selectAllByDeleteYn("N");
		
		return ResponseEntity.ok().body(acrossServiceList);
	}
	
	/**
	 * 연계 서비스 단건 조회 
	 * @return ResponseEntity<AcrossServiceDTO>
	 */
	@GetMapping("/api/v1/bff/across-service/{acrossServiceId}")
	@Operation(summary = "연계 서비스 단건 조회 API", description = "연계 서비스 단건 조회")
    @Schema(description = "연계 서비스 단건 조회 응답")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success")
    })
	public ResponseEntity<List<IntegratedAcrossServiceVO>> getAcrossServiceOne(@Parameter(name = "acrossServiceId", description = "연계 서비스 id", in = ParameterIn.PATH) @PathVariable Long acrossServiceId) {
		log.info("[AcrossServiceApiController] getAcrossServiceOne - 연계 서비스 단건 조회");
		log.info("id : {}", acrossServiceId);
		
		List<IntegratedAcrossServiceVO> list = acrossServiceService.selectInstanceListByAcrossServiceId(acrossServiceId);
		
		return ResponseEntity.ok().body(list);
	}
}