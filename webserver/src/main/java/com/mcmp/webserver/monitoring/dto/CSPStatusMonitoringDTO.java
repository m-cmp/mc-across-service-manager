package com.mcmp.webserver.monitoring.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(name = "CSPStatusMonitoringDTO", description = "상태별 모니터링 결과")
public class CSPStatusMonitoringDTO {

	public List<AgentStatusDTO> agent;
	public List<InstanceStatusDTO> instance;
	public List<ServiceStatusDTO> service;
	public List<AcrossServiceStatusDTO> acrossService;
}
