package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "CPUUsageDTO", 
description = "(usage_user / (usage_user + usage_system + usage_idle)) * 100% 계산 한 CPU Usage 결과")
public class CPUUsageDTO extends MonitoringValueDTO{

	@Schema(description = "필드 명")
	private String field;
	
	@Schema(description = "모니터링 지표")
	private MonitoringValueDTO values;
	
	private String host;

	public CPUUsageDTO(float value, String field) {
		super(value, field);
		// TODO Auto-generated constructor stub
	}
}
