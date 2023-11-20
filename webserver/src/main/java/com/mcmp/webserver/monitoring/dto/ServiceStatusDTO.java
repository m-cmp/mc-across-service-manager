package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ServiceStatusDTO", description = "서비스 상태 모니터링")
public interface ServiceStatusDTO {

	@Schema(description = "서비스 상태")
	public String getStatus();

	@Schema(description = "서비스 수")
	public Long getCnt();

	public static ServiceStatusDTO of(String getStatus, Long getCnt) {
		return new ServiceStatusDTOImpl(getStatus, getCnt);
	}
}
