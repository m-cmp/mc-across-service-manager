package com.mcmp.webserver.web.serviceinfo.dto;

import java.time.LocalDateTime;

import com.mcmp.webserver.web.serviceinfo.entity.ServiceInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "ServiceInfo", description = "서비스 정보")
public class ServiceInfoDTO {
	
	@Schema(description = "서비스 id")
	private String serviceId;
	
	@Schema(description = "서비스명")
	private String serviceName;
	
	@Schema(description = "서비스 템플릿 id")
	private Long serviceTemplateId;
	
	@Schema(description = "서비스 상태", allowableValues = {"ACTIVATE", "INACTIVATE"})
	private String serviceStatus;
	
	@Schema(description = "삭제 여부", allowableValues = {"Y", "N"})
	private String deleteYn;
	
	@Schema(description = "서비스 생성일시", example = "yyyyMMdd")
	private LocalDateTime serviceCreateDate;

	//DTO to Entity
	public ServiceInfo toEntity() {
		ServiceInfo serviceInfo = ServiceInfo.builder()
				.serviceId(serviceId)
				.serviceTemplateId(serviceTemplateId)
				.serviceStatus(serviceStatus)
				.deleteYn(deleteYn)
				.serviceCreateDate(serviceCreateDate)
				.build();
		return serviceInfo;
	}
	
	//Entity to DTO
	public static ServiceInfoDTO of(ServiceInfo serviceInfo) {
		return ServiceInfoDTO.builder()
				.serviceId(serviceInfo.getServiceId())
				.serviceTemplateId(serviceInfo.getServiceTemplateId())
				.serviceStatus(serviceInfo.getServiceStatus())
				.deleteYn(serviceInfo.getDeleteYn())
				.serviceCreateDate(serviceInfo.getServiceCreateDate())
				.build();
	}
}
