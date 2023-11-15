package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "MonitoringValueDTO", 
description = "")
public class MonitoringValueDTO {
	
	private float value;
	private String time;

}
