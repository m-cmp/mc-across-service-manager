package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.monitoring.dto.AcrossServiceStatusDTO;
import com.mcmp.webserver.monitoring.dto.AgentStatusDTO;
import com.mcmp.webserver.monitoring.dto.CSPResourceMonitoringDTO;
import com.mcmp.webserver.monitoring.dto.CSPStatusMonitoringDTO;
import com.mcmp.webserver.monitoring.dto.InstanceStatusDTO;
import com.mcmp.webserver.monitoring.dto.ServiceStatusDTO;
import com.mcmp.webserver.monitoring.repository.CSPMonitoringRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CSPService {

	@Autowired
	private CSPMonitoringRepository cspRepository;

	/**
	 * 연계 서비스 전체 목록 조회
	 * 
	 * @return List<AcrossServiceDTO>
	 */
	public List<CSPResourceMonitoringDTO> selectCSPResourceCnt() {
		return cspRepository.findCSPStatusMonitoring();
	}

	/**
	 * 상태별 모니터링 전체 조회
	 * 
	 * @return CSPStatusMonitoringDTO
	 */
	public CSPStatusMonitoringDTO selectCSPStatusMonitoringAll() {
		var across = cspRepository.findAcrossServicesStatusAll().stream().map(dto -> {
			return AcrossServiceStatusDTO.of(dto.getStatus(), dto.getCnt());
		}).collect(Collectors.toList());
		var agent = cspRepository.findAgentActiveYnAll().stream().map(dto -> {
			return AgentStatusDTO.of(dto.getStatus(), dto.getCnt());
		}).collect(Collectors.toList());
		var service = cspRepository.findServiceStatusAll().stream().map(dto -> {
			return ServiceStatusDTO.of(dto.getStatus(), dto.getCnt());
		}).collect(Collectors.toList());
		;
		var instancce = cspRepository.findInstanceStatusAll().stream().map(dto -> {
			return InstanceStatusDTO.of(dto.getStatus(), dto.getCnt());
		}).collect(Collectors.toList());
		;
		return new CSPStatusMonitoringDTO(agent, instancce, service, across);
	}
}
