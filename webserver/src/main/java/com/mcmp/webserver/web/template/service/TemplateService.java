package com.mcmp.webserver.web.template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.web.template.dto.TemplateDTO;
import com.mcmp.webserver.web.template.entity.Template;
import com.mcmp.webserver.web.template.repository.TemplateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 템플릿 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TemplateService {

	@Autowired
	private TemplateRepository templateRepository;

	/**
	 * 템플릿 전체 목록 조회
	 * 
	 * @return List<TemplateDTO>
	 */

	public List<TemplateDTO> selectAll() {
		List<Template> templateList = templateRepository.findAll();
		log.debug("[{}] {}", "selectAll", templateList.toString());

		return templateList.stream().map(TemplateDTO::of).collect(Collectors.toList());
	}


	/**
	 * 마이그레이션 타겟 서비스 템플릿 목록 조회
	 * @param serviceTemplateId
	 * @return List<TemplateDTO>
	 */
	public List<TemplateDTO> selectMigrationTargetTemplateList(Long serviceTemplateId) {
		List<Template> templateList = templateRepository.findMigrationTargetTemplateList(serviceTemplateId);
		log.info("[{}] {}", "selectMigrationTargetTemplateList", templateList.toString());

		return templateList.stream().map(TemplateDTO::of).collect(Collectors.toList());
	}

	
	/**
	 * 서비스 템플릿 삭제
	 *
	 * @param serviceTemplateId 삭제할 서비스 템플릿의 ID
	 * @return 삭제 성공 여부
	 */
	public boolean deleteTemplateById(Long serviceTemplateId) {
		log.info("서비스 템플릿 삭제 요청, ID: " + serviceTemplateId);
		try {
			// Repository를 사용하여 템플릿 삭제
			templateRepository.deleteById(serviceTemplateId);
			log.info("서비스 템플릿 삭제 성공, ID: " + serviceTemplateId);
			return true; // 삭제 성공
		} catch (Exception e) {
			log.error("서비스 템플릿 삭제 중 오류 발생: " + e.getMessage());
			return false; // 삭제 실패
		}
	}
}
