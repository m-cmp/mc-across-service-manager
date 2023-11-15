package com.mcmp.orchestrator.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 템플릿 파싱 용 TF path DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateTfPathDTO {
	
	@Schema(description = "map of csp as key vpc tf file path as value")
	private Map<String, String> vpc;
	@Schema(description = "map of csp as key vgw tf file path as value")
	private Map<String, String> vgw;
	@Schema(description = "map of csp as key vm tf file path as value")
	private Map<String, String> vm;
	@Schema(description = "map of csp as key vpn tf file path as value")
	private Map<String, String> vpn;
	@Schema(description = "map of csp as key gslb tf file path as value")
	private Map<String, String> gslb;
}
