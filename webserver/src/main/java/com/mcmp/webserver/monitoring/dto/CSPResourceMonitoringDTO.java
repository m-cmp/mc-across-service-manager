package com.mcmp.webserver.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CSPResourceMonitoringDTO", description = "모든 CSP 별 자원 수 모니터링 결과")
public interface CSPResourceMonitoringDTO {
	
	@Schema(description = "CSP 타입")
	public String getCsp();
	@Schema(description = "vpc 수")
	public Long getVpcCnt();
	@Schema(description = "인스턴스 수")
	public Long getInstanceCnt();
	@Schema(description = "어플리케이션 수")
	public Long getAppCnt();
	@Schema(description = "서비스 수")
	public Long getServiceCnt();
	@Schema(description = "gslb 수")
	public Long getGslbCnt();
	@Schema(description = "vpn 수")
	public Long getVpnCnt();
	@Schema(description = "연계서비스 수")
	public Long getAcrossServiceCnt();
}
