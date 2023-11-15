package com.mcmp.webserver.web.vpc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.web.vpc.dto.VPCDTO;
import com.mcmp.webserver.web.vpc.service.VPCService;

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
@Tag(name = "VPC", description = "VPC API")
@RequestMapping("/api/v1/bff/vpc")
public class VPCApiController {

	private final VPCService vpcService;

	/**
	 * VPC 전체 목록 조회 API
	 * @return ResponseEntity<List<VPCDTO>>
	 */
	@GetMapping("")
	@Operation(summary = "VPC 전체 목록 조회 API", description = "VPC 전체 목록 조회")
	@Schema(description = "VPC 목록 조회 응답")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success")
	})
	public ResponseEntity<List<VPCDTO>> getVPCList() {
		
		log.debug("[VPCApiController] getVPCList - VPC 목록 조회");
		
		List<VPCDTO> vpcList = vpcService.selectAll();
		
		return ResponseEntity.ok().body(vpcList);
	}
}
