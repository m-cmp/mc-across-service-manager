package com.mcmp.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 워크플로우 트리거 결과를 받는 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
	
	@JsonProperty("state")
	@Schema(description = "service status")
	private String serviceStatus;
	@Schema(description = "across service id")
	private String acrossServiceId;
	@Schema(description = "service id")
	private String serviceId;
	@Schema(description = "service template id")
	private String serviceTemplateId;
}
