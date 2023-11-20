package com.mcmp.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TF path DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TfPathDTO {
	
	@JsonProperty("vpc")
	@Schema(description = "vpc tf file path")
	private String vpc;
	@JsonProperty("vm")
	@Schema(description = "vm tf file path")
	private String vm;
	@JsonProperty("vgw")
	@Schema(description = "vgw tf file path")
	private String vgw;
	@JsonProperty("vpn")
	private String vpn;
	@JsonProperty("gslb")
	private String gslb;
}
