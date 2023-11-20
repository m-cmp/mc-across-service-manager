package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.NetResponseMonitoringRepository;

@Service
public class NetResponseService {

	@Autowired
	private NetResponseMonitoringRepository repository;

	/**
	 * Network Response 시간 추이 모니터링 정보 조회
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getNetResponseTimeTransByHostName(String hostName) {
		List<FluxRecord> result = repository.getNetResponseTimeTransByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}

}
