package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InstanceStatusDTO", description = "인스턴스 상태 모니터링")
public interface InstanceStatusDTO {
	
	@Schema(description = "인스턴스 상태")
	public String getStatus();
	@Schema(description = "VM 수")
	public Long getCnt();
	
	public static InstanceStatusDTO of(String getVmStatus, Long getVmCnt) {
        return new InstanceStatusDTOImpl(getVmStatus, getVmCnt);
    }
}	
