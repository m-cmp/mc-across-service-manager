package com.mcmp.orchestrator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 어플리케이션 엔티티 클래스
 */
@Data
@NoArgsConstructor
@Entity(name="tb_application")
public class ApplicationEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="application_id", nullable = false, length=20)
	private String applicationId;
	@Column(name="service_instance_id", nullable = false, length=250)
	private String serviceInstanceId;
	@Column(name="application_type", nullable = false, length=36)
	private String applicationType;
}
