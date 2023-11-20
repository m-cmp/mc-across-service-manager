package com.mcmp.orchestrator.instance.controller;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.orchestrator.common.ApiCommonResponse;
import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
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
 * SA Orchestrator 서비스/어플리케이션 컨트롤러 클래스
 * 
 * @details SA Orchestrator 서비스/어플리케이션 컨트롤러 클래스
 * @author 오승재
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orchestrator")
@Tag(name="Service instance", description="Service instance API")
public class InstanceController {
//	 http://localhost:8090/swagger-ui/index.html

	private final InstanceService instanceService;
	
	/**
	 * 서비스/어플리케이션 생성 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Service/Application instantiation API", description = "triggers intantiation workflow after parsing template and inserting service instance into inventory")
	@Schema(description = "Service/Application instantiation API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/services")
	public ApiCommonResponse<ServiceDTO> triggerInstantiation(@RequestBody TemplateDTO template) {
		log.info("[Service instantiation] templateDTO ["
	            + "serviceTemplateId={}]",
	            template.getServiceTemplateId());
		
		ApiCommonResponse<ServiceDTO> response= ApiCommonResponse.<ServiceDTO>builder()
				.code(HttpStatus.OK.value())
				.status(HttpStatus.OK)
				.message("서비스 생성 워크플로우 trigger 성공")
				.data(!ObjectUtils.isEmpty(template.getVpcId())
						? instanceService.triggerInstantiationWithVpc(template.getServiceTemplateId(), template.getVpcId())
						: instanceService.triggerInstantiation(template.getServiceTemplateId()))
				.build();
	
		return response;
	}

	/**
	 * 서비스/어플리케이션 삭제 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Service/Application termination API", description = "triggers termination workflow with given service id")
	@Schema(description = "Service/Application termination API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/services")
	public ApiCommonResponse<ServiceDTO> triggerTermination(@RequestParam(name="service_id") List<String> serviceIdList) {
		log.info("[Service termination] serviceIdList : {}", serviceIdList);
		
		ServiceDTO apiResponse = instanceService.triggerTermination(serviceIdList);
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("canceled".equals(apiResponse.getServiceStatus())? "서비스에 속한 VM이 없습니다.":"서비스 삭제 워크플로우 trigger 성공")
				.data(apiResponse)
				.build();
		
		return response;
	}
	
	/**
	 * 서비스/어플리케이션 전체 조회 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
//	@Operation(summary = "Service/Application retrieval API", description = "triggers retrieval workflow")
//	@Schema(description = "Service/Application retrieval API Response")
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "Success"),
//	})
//	@GetMapping("/services")
//	public ApiCommonResponse<ServiceDTO> triggerHealthCheck() {
//		String flag = "SERVICE";
//		ServiceDTO apiResponse = instanceService.triggerScheduledHealthCheck(flag);
//		
//		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
//				.code(200)
//				.status(HttpStatus.OK)
//				.message(ObjectUtils.isEmpty(apiResponse)? "서비스가 없습니다.":"서비스 전체 조회 워크플로우 trigger 성공")
//				.data(apiResponse)
//				.build();
//		
//		return response;
//	}
	
	/**
	 * 서비스/어플리케이션 조회 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Service/Application retrieval API", description = "triggers retrieval workflow with given service id")
	@Schema(description = "Service/Application retrieval API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@GetMapping("/services/{serviceId}")
	public ApiCommonResponse<ServiceDTO> triggerHealthCheck(@PathVariable String serviceId) {
		log.info("[Service health check] serviceId : {}", serviceId);
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("서비스 조회 워크플로우 trigger 성공")
				.data(instanceService.triggerHealthCheck(serviceId))
				.build();
		
		return response;
	}

	/**
	 * 모니터링 Agent 배포 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Deploying monitoring agent API", description = "triggers deploying monitoring agent to vm instance with given service id")
	@Schema(description = "Deploying monitoring agent API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/services/{serviceId}/collectors")
	public ApiCommonResponse<ServiceDTO> triggerAgentDeploy(@PathVariable String serviceId) {
		log.info("[Deploy monitoring agent to service] serviceId : {}", serviceId);
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("모니터링 Agent 배포 워크플로우 trigger 성공")
				.data(instanceService.triggerAgentDeploy(serviceId))
				.build();
		
		return response;
	}
	
	/**
	 * 모니터링 Agent 인스턴스에 배포 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Deploying monitoring agent API", description = "triggers deploying monitoring agent to vm instance with given service id")
	@Schema(description = "Deploying monitoring agent API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/services/service-instances/{serviceInstanceId}/collectors")
	public ApiCommonResponse<ServiceDTO> triggerAgentDeployToInstance(@PathVariable String serviceInstanceId) {
		log.info("[Deploy monitoring agent to instance] serviceInstanceId : {}", serviceInstanceId);
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("모니터링 Agent 인스턴스에 배포 워크플로우 trigger 성공")
				.data(instanceService.triggerAgentDeployToInstance(serviceInstanceId))
				.build();
		
		return response;
	}
	
	/**
	 * 서비스 마이그레이션 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Service Migration API", description = "triggers service migration worflow")
	@Schema(description = "Service Migration API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PutMapping("/services/{serviceId}/migration")
	public ApiCommonResponse<ServiceDTO> migrateService(@PathVariable String serviceId, @RequestBody TemplateDTO template) {
		log.info("[Service migration] serviceId : {}, " +
				"template[serviceTemplateId={}]",
				serviceId,
				template.getServiceTemplateId());
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("서비스 마이그레이션 워크플로우 trigger 성공")
				.data(instanceService.migrateService(serviceId, template.getServiceTemplateId()))
				.build();
		
		return response;
	}
	
	/**
	 * 마이그레이션 기존 데이터 삭제 API
	 * 
	 * @return ApiCommonResponse<ServiceDTO>
	 * @see com.mcmp.orchestrator.dto.spring.orchestrator.dto.ServiceDTO
	 */
	@Operation(summary = "Delete original instance API", description = "triggers termination workflow with given service id and instance id")
	@Schema(description = "Service/Application termination API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/services/{serviceId}")
	public ApiCommonResponse<ServiceDTO> triggerTerminationForMigration(@PathVariable String serviceId, @RequestBody ServiceInstanceDTO service) {
		log.info("[Service migration - delete original data] serviceId : {}, "
				+ "service[serviceInstanceId={},"
				+ "applicationId={}]", 
				serviceId, 
				service.getServiceInstanceId(),
				service.getApplicationId());
		
		ApiCommonResponse<ServiceDTO> response = ApiCommonResponse.<ServiceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("서비스 삭제 워크플로우 trigger 성공")
				.data(instanceService.triggerTerminationForMigration(serviceId, service))
				.build();
		
		return response;
	}
	
	@Scheduled(fixedDelay = 300000)
	public void scheduledServiceFailureCheck() {
		
		String flag = "SERVICE";
		instanceService.triggerScheduledHealthCheck(flag);
//		instanceService.triggerServiceFailureCheck();
	}
}
