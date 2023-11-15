package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.ProcessMonitoringRepository;

@Service
public class ProcessService {

	@Autowired
	private ProcessMonitoringRepository processRepository;
	
	/**
	 * 프로세스 수 추이 변화
	 * @return
	 */
	public List<Map<String, Object>> getProcessTransition() {
		List<FluxRecord> process = processRepository.getProcessTransition();

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = process.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
	
	/**
	 * 1개의 인스턴스의 시간 내 프로스세나 필드의 평균 수
	 * @param field 
	 * @param hostName
	 * @param minutes
	 * @return
	 */
	public List<Map<String, Object>> getFieldCntByHostAndMunutes(String field, String hostName, Integer minutes ) {
		List<FluxRecord> process = processRepository.getFieldCntByHostAndMunutes(field, hostName, minutes);
		
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = process.stream().map(record -> record.getValues())
				.collect(Collectors.toList());
		
		return values;
	}
	
	/**
	 * 인스턴스의 프로세스 상태별 수 추이
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getProcessStatusCntTransition(String hostName) {
		List<FluxRecord> process = processRepository.getProcessStatusCntTransition(hostName);
		
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = process.stream().map(record -> record.getValues())
				.collect(Collectors.toList());
		
		return values;
	}
	
	
	
	public List<Map<String, Object>> getThreadTransition() {
		List<FluxRecord> process = processRepository.getThreadTransition();

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = process.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}

}
