package com.mcmp.orchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 서비스 템플릿 엔티티 클래스
 */
@Data
@NoArgsConstructor
@Entity(name="tb_service_template")
public class TemplateEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="service_template_id", nullable=false, length=20)
	private String serviceTemplateId;
	@Column(name="service_template_name", nullable=false, length=50)
	private String serviceTemplateName;
	@Column(name="service_template_path", nullable=false, length=100)
	private String serviceTemplatePath;
	@Column(name="service_template_create_date", nullable=false)
	private String serviceTemplateCreateDate;
	@Column(name="across_type", nullable=true, length=12)
	private String acrossType;
	@Column(name="target_csp1", nullable=false, length=5)
	private String targetCsp1;
	@Column(name="target_csp2", nullable=false, length=5)
	private String targetCsp2;
}
