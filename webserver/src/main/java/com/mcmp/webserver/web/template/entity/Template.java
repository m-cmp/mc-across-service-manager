package com.mcmp.webserver.web.template.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 템플릿 Entity
 */
@Entity
@Table(name = "tb_service_template")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Template {

	/*
	 * 서비스 템플릿 id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "service_template_id", nullable = false, length = 20)
	private Long serviceTemplateId;

	/*
	 * 서비스 템플릿명
	 */
	@Column(name = "service_template_name", nullable = false, length = 50)
	private String serviceTemplateName;

	/*
	 * 템플릿 서비스 타입
	 */
	@Column(name = "template_service_type")
	private Long templateServiceType;
	
	/*
	 * 연계 타입 [VPC, GSLB, ETC]
	 */
	@Column(name = "across_type", length = 12)
	private String acrossType;

	/*
	 * 인스턴스화 대상 CSP1
	 */
	@Column(name = "target_csp1", nullable = false, length = 5)
	private String targetCsp1;

	/*
	 * 인스턴스화 대상 CSP2
	 */
	@Column(name = "target_csp2", nullable = false, length = 5)
	private String targetCsp2;

	/*
	 * 서비스 템플릿 파일 경로
	 */
	@Column(name = "service_template_path", nullable = false, length = 100)
	private String serviceTemplatePath;

	/*
	 * 서비스 템플릿 생성일시
	 */
	@Column(name = "service_template_create_date", nullable = false)
	private LocalDateTime serviceTemplateCreateDate;

	

	@Builder
	public Template(Long serviceTemplateId, String serviceTemplateName, Long templateServiceType, String acrossType, String targetCsp1,
			String targetCsp2, String serviceTemplatePath, LocalDateTime serviceTemplateCreateDate) {
		this.serviceTemplateId = serviceTemplateId;
		this.serviceTemplateName = serviceTemplateName;
		this.templateServiceType = templateServiceType;
		this.acrossType = acrossType;
		this.targetCsp1 = targetCsp1;
		this.targetCsp2 = targetCsp2;
		this.serviceTemplatePath = serviceTemplatePath;
		this.serviceTemplateCreateDate = serviceTemplateCreateDate;
	}

}
