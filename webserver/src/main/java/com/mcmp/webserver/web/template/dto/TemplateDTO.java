package com.mcmp.webserver.web.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.mcmp.webserver.web.template.entity.Template;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "Template", description = "서비스 템플릿 정보")
public class TemplateDTO {

	@Schema(description = "서비스 템플릿 id")
	private Long serviceTemplateId;

	@Schema(description = "서비스 템플릿명")
	private String serviceTemplateName;
	
	@Schema(description = "서비스 템플릿 타입")
	private Long templateServiceType;
	
	@Schema(description = "연계 타입")
	private String acrossType;

	@Schema(description = "인스턴스화 대상 CSP1")
	private String targetCsp1;

	@Schema(description = "인스턴스화 대상 CSP2")
	private String targetCsp2;

	@Schema(description = "서비스 템플릿 파일 경로")
	private String serviceTemplatePath;

	@Schema(description = "서비스 템플릿 생성일시")
	private LocalDateTime serviceTemplateCreateDate;

	public Template toEntity() {
		Template serviceTemplate = Template.builder()
				.serviceTemplateId(serviceTemplateId)
				.serviceTemplateName(serviceTemplateName)
				.templateServiceType(templateServiceType)
				.acrossType(acrossType)
				.targetCsp1(targetCsp1)
				.targetCsp2(targetCsp2)
				.serviceTemplatePath(serviceTemplatePath)
				.serviceTemplateCreateDate(serviceTemplateCreateDate).
				build();
		return serviceTemplate;
	}

	public static TemplateDTO of(Template template) {
		return TemplateDTO.builder()
				.serviceTemplateId(template.getServiceTemplateId())
				.serviceTemplateName(template.getServiceTemplateName())
				.templateServiceType(template.getTemplateServiceType())
				.acrossType(template.getAcrossType())
				.targetCsp1(template.getTargetCsp1())
				.targetCsp2(template.getTargetCsp2())
				.serviceTemplatePath(template.getServiceTemplatePath())
				.serviceTemplateCreateDate(template.getServiceTemplateCreateDate())
				.build();
	}
}