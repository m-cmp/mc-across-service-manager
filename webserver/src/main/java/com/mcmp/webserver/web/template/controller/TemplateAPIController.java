package com.mcmp.webserver.web.template.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@Tag(name = "Template", description = "템플릿 API")
public class TemplateAPIController {
	private final TemplateService templateService;

	/**
	 * 템플릿 전체 목록 응답 API
	 * 
	 * @return ResponseEntity<List<TemplateDTO>>
	 */
	@GetMapping("/api/v1/bff/template")
	@Operation(summary = "템플릿 목록 조회 API", description = "템플릿 목록 조회")
	@Schema(description = "템플릿 목록 조회 응답")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success")})
	public ResponseEntity<List<TemplateDTO>> getTemplateList() {
		log.debug("[TemplateAPIController] getTemplateList - 템플릿 목록 조회");
		List<TemplateDTO> TemplateList = templateService.selectAll();

		return ResponseEntity.ok().body(TemplateList);
	}
	
	/**
	 * 마이그레이션 타겟 템플릿 목록 조회
	 * @param serviceTemplateId
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/api/v1/bff/template/migration/{serviceTemplateId}")
	@Operation(summary = "마이그레이션 템플릿 목록 조회 API", description = "마이그레이션 템플릿 목록 조회")
	@Schema(description = "마이그레이션 템플릿 목록 조회 응답")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success")
	})
	public ResponseEntity<List<TemplateDTO>> getMigrationTargetTemplateList(@Parameter(name = "serviceTemplateId", description = "서비스 템플릿 id", in = ParameterIn.PATH) @PathVariable(required = true) Long serviceTemplateId) throws Exception {
		log.info("[TemplateAPIController] getMigrationTargetTemplateList - 마이그레이션 템플릿 목록 조회");
		
		List<TemplateDTO> templateList = templateService.selectMigrationTargetTemplateList(serviceTemplateId);
		
		return ResponseEntity.ok().body(templateList);
	}
	

    @DeleteMapping("/api/v1/bff/template/delete")
    @Operation(summary = "템플릿 삭제 API", description = "템플릿 삭제")
    public ResponseEntity<?> templateDeleteOrder(
        @RequestParam(name = "serviceTemplateId") Long serviceTemplateId
    ) {
        log.info("템플릿 삭제 요청");
        try {
            templateService.deleteTemplateById(serviceTemplateId);
            log.info("삭제 대상 템플릿 ID : " + serviceTemplateId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("템플릿 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("템플릿 삭제 중 오류 발생");
        }
    }

}
