package com.mcmp.orchestrator.across.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.orchestrator.across.service.AcrossService;
import com.mcmp.orchestrator.common.ApiCommonResponse;
import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.TemplateDTO;
import com.mcmp.orchestrator.instance.service.InstanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AcrossController
 * 
 * @details 연계서비스 관련 api 컨트롤러 클래스
 * @author 오승재
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Across-service", description = "Across-service API")
@RequestMapping("/api/v1/orchestrator")
public class AcrossController {

	private final AcrossService acrossService;
	private final InstanceService instanceService;

	/**
	 * 연계 서비스 생성 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Across-service Instantiation API", description = "triggers across service after parsing template and inserting across service instance into inventory")
	@Schema(description = "Across-service Instantiation API Response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	@PostMapping("/across-services")
	public ApiCommonResponse<ServiceDTO> triggerAcrossInstantiation(@RequestBody TemplateDTO template) {
		log.info("[Across service instantiation] template ["
	            + "serviceTemplateId={}]",
	            template.getServiceTemplateId());

		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder().code(200).status(HttpStatus.OK)
				.message("연계 서비스 생성 워크플로우 trigger 성공")
				.data(!ObjectUtils.isEmpty(template.getVpcId())
						? acrossService.triggerAcrossInstantiationWithVpc(template.getServiceTemplateId(), template.getVpcId())
						: acrossService.triggerAcrossInstantiation(template.getServiceTemplateId()))
				.build();

		return response;
	}

	/**
	 * 연계 서비스 삭제 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Across-service Termination API", description = "triggers across service termination workflow after checking service instance")
	@Schema(description = "Across-service Termination API Response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	@DeleteMapping("/across-services")
	public ApiCommonResponse<ServiceDTO> triggerAcrossTermination(
			@RequestParam(name = "across_service_id") List<String> serviceIdList, @RequestParam(name="vm_only_yn", required=true) String vmOnlyYn) {
		log.info("[Across service termination] vmOnlyYn : {}, "
				+ "serviceIdList : {}", 
				vmOnlyYn, 
				serviceIdList);
		
		ServiceDTO apiResponse = acrossService.triggerAcrossTermination(vmOnlyYn, serviceIdList);
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder().code(200).status(HttpStatus.OK)
				.message("canceled".equals(apiResponse.getServiceStatus())? "연계서비스에 속한 VM이 없습니다.":"연계 서비스 삭제 워크플로우 trigger 성공")
				.data(apiResponse)
				.build();

		return response;
	}

	/**
	 * 연계 서비스 전체 조회 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
//	@Operation(summary = "Across-service Retrieval API", description = "triggers health check of across service with given across service id")
//	@Schema(description = "Across-service Retrieval API Response")
//	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
//	@GetMapping("/across-services")
//	public ApiCommonResponse<ServiceDTO> triggerAcrossHealthCheck() {
//		String flag = "ACROSS";
//		ServiceDTO apiResponse = instanceService.triggerScheduledHealthCheck(flag);
//		
//		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
//				.code(200)
//				.status(HttpStatus.OK)
//				.message(ObjectUtils.isEmpty(apiResponse)?"연계 서비스가 없습니다.":"연계 서비스 상태 조회 워크플로우 trigger 성공")
//				.data(apiResponse)
//				.build();
//
//		return response;
//	}
	
	/**
	 * 연계 서비스 조회 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Across-service Retrieval API", description = "triggers health check of across service with given across service id")
	@Schema(description = "Across-service Retrieval API Response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	@GetMapping("/across-services/{acrossServiceId}")
	public ApiCommonResponse<ServiceDTO> triggerAcrossHealthCheck(@PathVariable String acrossServiceId) {
		log.info("[Across service health check] acrossServiceId : {}", acrossServiceId);
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("연계 서비스 상태 조회 워크플로우 trigger 성공")
				.data(acrossService.triggerAcrossHealthCheck(acrossServiceId))
				.build();
		
		return response;
	}

	/**
	 * 연계서비스 모니터링 Agent 배포 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Deploying monitoring agent API", description = "triggers deploying monitoring agent to vm instance with given service id")
	@Schema(description = "Deploying monitoring agent API Response")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Success"), })
	@PostMapping("/across-services/{acrossServiceId}/collectors")
	public ApiCommonResponse<ServiceDTO> triggerAgentDeploy(@PathVariable String acrossServiceId) {
		log.info("[Deploy monitoring agent to across service] acrossServiceId : {}", acrossServiceId);

		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("모니터링 Agent 배포 워크플로우 trigger 성공")
				.data(acrossService.triggerAcrossAgentDeploy(acrossServiceId))
				.build();

		return response;
	}
	
	@Scheduled(fixedDelay = 300000)
	public void scheduledAcrossFailureCheck() {
		String flag = "ACROSS";
		instanceService.triggerScheduledHealthCheck(flag);
	}
}
