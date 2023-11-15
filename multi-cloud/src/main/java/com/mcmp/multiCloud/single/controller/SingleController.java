package com.mcmp.multiCloud.single.controller;

import java.io.IOException;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmp.multiCloud.common.ApiCommonResponse;
import com.mcmp.multiCloud.dto.InstanceDTO;
import com.mcmp.multiCloud.single.service.SingleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Multi-Cloud 단일 서비스 컨트롤러 클래스
 * 
 * @details Multi-Cloud 단일 서비스 컨트롤러 클래스
 * @author 박성준
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/multi-cloud")
public class SingleController {

	private final SingleService singleService;
	private final HttpServletRequest request;
	/**
	 * 단일 서비스 인스턴스 생성 API
	 * 
	 * @return ApiCommonResponse<Map<String, String>>
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VM Instance Create API", description = "Create directory with 'service instance id', copy tf file and Create VM Instance")
	@Schema(description = "Single Service Instance Create API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@PostMapping("/services")
	public ResponseEntity<ApiCommonResponse<Map<String, String>>> createInstance(@RequestBody InstanceDTO dto) throws IOException, ParseException{
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[SingleController[createInstance]] InstanceDTO : {}", dto);
		ApiCommonResponse<Map<String, String>> response = ApiCommonResponse.<Map<String, String>>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("싱글 서비스 인스턴스 생성 성공")
				.data(singleService.createInstance(dto))
				.build();
		// 생성한 vm 인스턴스의 sevice_id, public_ip return
		return ResponseEntity.ok().body(response);


	}
	/**
	 * 실글 서비스 인스턴스 삭제 API
	 * 
	 * @return ApiCommonResponse<String>
	 * @throws ParseException 
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VM Instance Delete API", description = "Delete created VM Instances and directory")
	@Schema(description = "Single Service Instance Delete API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@DeleteMapping("/services")
	public ResponseEntity<ApiCommonResponse<String>> deleteInstance(@RequestBody InstanceDTO dto) throws IOException, ParseException {
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[SingleController[delete Instance]] InstanceDTO : {}", dto);
		ApiCommonResponse<String> response = ApiCommonResponse.<String>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("싱글 서비스 인스턴스 삭제 성공")
				.data(singleService.deleteInstance(dto))
				.build();
		
		// 삭제 메시지 return
		return ResponseEntity.ok().body(response);
	
	}
	/**
	 * 인스턴스 health check API
	 * 
	 * @return ApiCommonResponse<String>
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @see com.mcmp.multiCloud.common
	 */
	@Operation(summary = "VM Instance Health Check API", description = "Health Check with 'vm instance id'")
	@Schema(description = "VM Instance Health Check API Response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success"),
	})
	@GetMapping("/services")
	public ResponseEntity<ApiCommonResponse<InstanceDTO>> healthCheck(@RequestParam("service_instance_id") String serviceInstanceId, 
			@RequestParam("csp") String csp, @RequestParam("vm_instance_id") String vmInstacneId) throws IOException {
		log.info("[API] "+ request.getMethod() + " " + request.getServletPath());
		log.info("[SingleController[health check]] vmInstacneId : {}", vmInstacneId);
		ApiCommonResponse<InstanceDTO> response = ApiCommonResponse.<InstanceDTO>builder()
				.code(200)
				.status(HttpStatus.OK)
				.message("Health Check 성공")
				.data(singleService.healthCheck(serviceInstanceId, csp, vmInstacneId))
				.build();
		
		// vm 인스턴스에 대한 status return
		return ResponseEntity.ok().body(response);
	
	}
}
