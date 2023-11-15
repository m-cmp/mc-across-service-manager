package com.mcmp.orchestrator.info.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.orchestrator.common.ApiCommonResponse;
import com.mcmp.orchestrator.dto.ServiceDTO;
import com.mcmp.orchestrator.dto.ServiceInstanceDTO;
import com.mcmp.orchestrator.entity.AcrossServiceEntity;
import com.mcmp.orchestrator.entity.ServiceInfoEntity;
import com.mcmp.orchestrator.info.service.InfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InfoController
 * 
 * @details 서비스 인스턴스 정보 관리를 위한 컨트롤러 클래스
 * @author 오승재
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orchestrator/info")
@Tag(name = "Instance info", description = "Instance info management API")
public class InfoController {
	
	private final InfoService infoService;
	
	/**
	 * 진행 상황 업데이트 API
	 * 
	 * @return ApiCommonResponse<ServiceInfoEntity>
	 * @see com.mcmp.orchestrator.entity.ServiceInfoEntity
	 */
	@Operation(summary = "service instantiation workflow state update API", description = "updates instantiation workflow state when each tasks is completed")
	@Schema(description = "update workflowState Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PutMapping("/services/{serviceId}/state")
	public ApiCommonResponse<ServiceInfoEntity> updateWorkflowState(@PathVariable String serviceId, @RequestBody ServiceDTO serviceDTO) {
		log.info("[Service workflow check] serviceDTO : {}", serviceDTO);
		
		serviceDTO.setServiceId(serviceId);
		ApiCommonResponse<ServiceInfoEntity> response = ApiCommonResponse.<ServiceInfoEntity>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("워크플로우 상태 업데이트 성공")
				.data(infoService.updateWorkflowState(serviceDTO))
				.build();
		
		return response;
	}
	
	/**
	 * 서비스 상태 헬스체크 후 업데이트 API
	 * 
	 * @return ApiCommonResponse<Object>
	 */
	@Operation(summary = "Service status update API", description = "updates Service status")
	@Schema(description = "update service status Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PutMapping("/status")
	public ApiCommonResponse<Object> updateServiceStatus(@RequestBody List<ServiceInstanceDTO> statusList) {
		log.info("[Service health check] statusList : {}", statusList);
		
		ApiCommonResponse<Object> response = ApiCommonResponse.<Object>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("서비스 상태 업데이트 성공")
				.data(infoService.updateServiceStatus(statusList))
				.build();
		
		return response;
	}
	
	/**
	 * 서비스 인벤토리에서 삭제 API
	 * 
	 * @return ApiCommonResponse<Object> 
	 */
	@Operation(summary = "Delete service instance data API", description = "deletes service instance data from inventory with given service id")
	@Schema(description = "Delete service instance data API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/services")
	public ApiCommonResponse<Object> deleteService(@RequestParam(name="service_id") List<String> serviceIdList, @RequestParam(name="is_migrating", defaultValue="N") String isMigrating) {
		
		ApiCommonResponse<Object> response = ApiCommonResponse.<Object>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("Y".equals(isMigrating.toUpperCase())? "서비스 마이그레이션":"서비스 삭제 성공")
				.data("Y".equals(isMigrating.toUpperCase())? "Migrating":infoService.deleteService(serviceIdList))
				.build();
		
		return response;
	}
	
	/**
	 * 연계서비스 진행 상황 업데이트 API
	 * 
	 * @return ApiCommonResponse<AcrossServiceEntity>
	 * @see com.mcmp.orchestrator.entity.AcrossServiceEntity
	 */
	@Operation(summary = "across service instantiation workflow state update API", description = "updates instantiation workflow state when each tasks is completed")
	@Schema(description = "update workflowState Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PutMapping("/across-services/{acrossServiceId}/state")
	public ApiCommonResponse<AcrossServiceEntity> updateAcrossWorkflowState(@PathVariable String acrossServiceId, @RequestBody ServiceDTO workflowDTO) {
		
		workflowDTO.setAcrossServiceId(acrossServiceId);
		ApiCommonResponse<AcrossServiceEntity> response = ApiCommonResponse.<AcrossServiceEntity>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("워크플로우 상태 업데이트 성공")
				.data(infoService.updateAcrossWorkflowState(workflowDTO))
				.build();
		
		return response;
	}
	
	/**
	 * 연계 서비스 인벤토리에서 삭제 API
	 * 
	 * @return ApiCommonResponse<Object> 
	 */
	@Operation(summary = "Delete across service instance data API", description = "deletes across service instance data from inventory with given across service id")
	@Schema(description = "Delete across service instance data API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/across-services")
	public ApiCommonResponse<Object> deleteAcrossService(@RequestParam(name="across_service_id") List<String> acrossServiceIdList) {
		log.info("acrossServiceIdList : {}", acrossServiceIdList);
		ApiCommonResponse<Object> response = ApiCommonResponse.<Object>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("연계 서비스 삭제 성공")
				.data(infoService.deleteAcrossService(acrossServiceIdList))
				.build();
		
		return response;
	}
	
	// 에어플로우에서 BFF로 워크플로우 끝났다고 알려주는 API - 이거는 대그에서 마지막에 쏴주는 게 낫나?
}
