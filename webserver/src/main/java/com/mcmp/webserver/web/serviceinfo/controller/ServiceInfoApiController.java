package com.mcmp.webserver.web.serviceinfo.controller;

import java.net.BindException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceListVO;
import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceVO;
import com.mcmp.webserver.web.serviceinfo.dto.IntegratedServiceWithTemplatesVO;
import com.mcmp.webserver.web.serviceinfo.service.ServiceInfoService;
import com.mcmp.webserver.web.template.dto.TemplateDTO;
import com.mcmp.webserver.web.template.service.TemplateService;

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
@Tag(name = "Service", description = "서비스 API")
@RequestMapping("/api/v1/bff")
public class ServiceInfoApiController {

	private final ServiceInfoService serviceInfoService;
	
	private final TemplateService templateService;
	
	/**
	 * 단일 서비스 목록 조회
	 * @return ResponseEntity<List<IntegratedServiceListVO>>
	 * @throws HttpRequestMethodNotSupportedException 
	 * @throws Exception 
	 */
	@GetMapping("/service")
	@Operation(summary = "단일 서비스 목록 조회 API", description = "단일 서비스 목록 조회")
	@Schema(description = "단일 서비스 목록 조회 응답")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success")
	})
	public ResponseEntity<List<IntegratedServiceListVO>> getServiceInfoList() {
		log.debug("[ServiceInfoApiController] getServiceInfoList - 단일 서비스 목록 조회");
		
		List<IntegratedServiceListVO> serviceInfoList = serviceInfoService.selectAll();
		
		return ResponseEntity.ok().body(serviceInfoList);
	}

	/**
	 * 단일 서비스 상세 조회 
	 * @return ResponseEntity<IntegratedServiceVO>
	 * @throws BindException 
	 */
	@GetMapping("/service/{serviceId}")
	@Operation(summary = "서비스 상세 조회 API", description = "서비스 상세 조회")
    @Schema(description = "서비스 상세 조회 응답")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success")
    })
	public ResponseEntity<IntegratedServiceWithTemplatesVO> getServiceInfoOne(@Parameter(name = "serviceId", description = "서비스 id", in = ParameterIn.PATH) @PathVariable(required = true) Long serviceId) {
		log.info("[ServiceInfoApiController] getServiceInfoOne - 서비스 상세 조회");
		log.info("id : {}", serviceId);
	
		
		IntegratedServiceVO vo = serviceInfoService.selectInstanceByServiceId(serviceId);
		
		List<TemplateDTO> templateList = templateService.selectMigrationTargetTemplateList(Long.parseLong(vo.getServiceTemplateId()));
		
		IntegratedServiceWithTemplatesVO iswt = new IntegratedServiceWithTemplatesVO(vo, templateList);
		
		return ResponseEntity.ok().body(iswt);
	}
}
