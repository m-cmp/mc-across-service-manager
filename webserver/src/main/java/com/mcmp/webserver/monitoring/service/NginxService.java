package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.NginxMonitoringRepository;

@Service
public class NginxService {

	@Autowired
	private NginxMonitoringRepository repository;

	/**
	 * Nginx TPS 모니터링 정보 조회
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getNginxTpsByHostName(String hostName) {
		List<FluxRecord> result = repository.getNginxTpsByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
	
	/**
	 * Nginx TPS 추이 조회 (1H)
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getNginxTpsTransitionByHostName(String hostName) {
		List<FluxRecord> result = repository.getNginxTpsTransitionByHostName(hostName);
		
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());
		
		return values;
	}

	/**
	 * Nginx Active 모니터링 정보 조회
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getNginxActiveByHostName(String hostName) {
		List<FluxRecord> result = repository.getNginxActiveByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
	
	/**
	 * Nginx Active 추이
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getNginxActiveTransByHostName(String hostName) {
		List<FluxRecord> result = repository.getNginxActiveTransByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
}
