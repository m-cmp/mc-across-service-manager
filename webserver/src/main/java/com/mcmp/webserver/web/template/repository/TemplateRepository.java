package com.mcmp.webserver.web.template.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mcmp.webserver.web.template.entity.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {

	// 템플릿 전체 목록 조회
	List<Template> findAll();

	// 서비스 마이그레이션 타겟 템플릿 목록 조회
	@Query(
		nativeQuery = true,
		value = """
				SELECT 
						ST.service_template_id,
						ST.service_template_name,
						ST.template_service_type,
						ST.across_type,
						ST.target_csp1,
						ST.target_csp2,
						ST.service_template_path,
						ST.service_template_create_date
				  FROM 	tb_service_template ST
				  WHERE 1=1
				  AND 	ST.across_type = 'NONE'
				  AND	ST.target_csp1 != ( 
				  			SELECT target_csp1
							  FROM tb_service_template
							 WHERE service_template_id = :serviceTemplateId 
					 	)
				  AND 	ST.template_service_type = ( 
				  			SELECT template_service_type
							  FROM tb_service_template
						 	 WHERE service_template_id = :serviceTemplateId 
						)
				"""
	)
	List<Template> findMigrationTargetTemplateList(@Param("serviceTemplateId") Long serviceTemplateId);
}
