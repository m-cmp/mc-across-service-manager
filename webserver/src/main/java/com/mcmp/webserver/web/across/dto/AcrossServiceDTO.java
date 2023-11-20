package com.mcmp.webserver.web.across.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.mcmp.webserver.web.across.entity.AcrossService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "AcrossService", description = "연계 서비스 정보")
public class AcrossServiceDTO {
	
	@NotBlank(message = "연계 서비스 id는 필수 값입니다.")
	@Schema(description = "연계 서비스 id")
	private Long acrossServiceId;
	
	@Schema(description = "연계 서비스명")
	private String acrossServiceName;
	
	@Schema(description = "연계 서비스 상태", allowableValues = {"ON", "OFF"})
	private String acrossStatus;
	
	@Schema(description = "연계 서비스 타입")
	private String acrossType;
	
	@Schema(description = "삭제 여부", allowableValues = {"Y", "N"})
	private String deleteYn;
	
//	@DateTimeFormat(pattern = "yyyyMMdd")
	@Schema(description = "연계 서비스 생성일시", example = "yyyyMMdd")
	private String acrossCreateDate;
	
	//DTO to Entity
	public AcrossService toEntity() {
		AcrossService acrossService = AcrossService.builder()
					.acrossServiceId(acrossServiceId)
					.acrossServiceName(acrossServiceName)
					.acrossStatus(acrossStatus)
					.acrossType(acrossType)
					.deleteYn(deleteYn)
					.acrossCreateDate(LocalDateTime.parse(acrossCreateDate))
					.build();
		return acrossService;
	}
	
	//Entity to DTO
	public static AcrossServiceDTO of(AcrossService acrossService) {
		return AcrossServiceDTO.builder()
				.acrossServiceId(acrossService.getAcrossServiceId())
				.acrossServiceName(acrossService.getAcrossServiceName())
				.acrossStatus(acrossService.getAcrossStatus())
				.acrossType(acrossService.getAcrossType())
				.deleteYn(acrossService.getDeleteYn())
				.acrossCreateDate(acrossService.getAcrossCreateDate().toString())
				.build();
	}
}
