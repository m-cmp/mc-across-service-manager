package com.mcmp.orchestrator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcmp.orchestrator.entity.TemplateEntity;

/**
 * 서비스 템플릿 Repository
 */
@Repository
public interface ServiceTemplateRepository extends JpaRepository<TemplateEntity, String> {
	
	TemplateEntity findByServiceTemplateId(String templateId);
}
