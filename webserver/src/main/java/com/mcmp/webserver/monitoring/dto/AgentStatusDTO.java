package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AgentStatusDTO", description = "Agent 활성화 수 모니터링")
public interface AgentStatusDTO {
	
	@Schema(description = "Agent 활성화 상태")
	public String getStatus();
	@Schema(description = "Agent 활성화 수")
	public Long getCnt();
	
	public static AgentStatusDTO of(String getStatus, Long getCnt) {
        return new AppStatusDTOImpl(getStatus, getCnt);
    }
}