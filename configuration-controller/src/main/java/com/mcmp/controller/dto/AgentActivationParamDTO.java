package com.mcmp.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
/**
*
* @author : Jihyeong Lee
* @Project : mcmp-conf/controller
* @version : 1.0.0
* @date : 11/7/23
* @class-description : dto class for monitoring-agent
*
**/
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AgentActivationParamDTO {
	@JsonAlias("serviceInstanceId")
	private String serviceInstanceId;
	@JsonAlias("vmInstancePublicIp")
	private String vmInstancePublicIp;
	@JsonAlias("agentActivateYN")
	private String agentActivateYN;
	@JsonAlias("pingTargetUrl")
	private String pingTargetUrl;
	@JsonAlias("acrossType")
	private String acrossType;
}
