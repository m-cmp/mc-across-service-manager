package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AcrossServiceStatusDTO", description = "연계서비스 상태 모니터링")
public interface AcrossServiceStatusDTO {
	
	@Schema(description = "인스턴스 상태")
	public String getStatus();
	@Schema(description = "VM 수")
	public Long getCnt();
	
	public static AcrossServiceStatusDTO of(String getStatus, Long getCnt) {
        return new AcrossServiceStatusDTOImpl(getStatus, getCnt);
    }
	
}
