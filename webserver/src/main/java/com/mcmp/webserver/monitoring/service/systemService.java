package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.SystemMonitoringRepository;

@Service
public class systemService {

	@Autowired
	private SystemMonitoringRepository systemRepository;

	/**
	 * 인스턴스의 uptime 시간 구하기
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getUptimeByHostName(String hostName) {
		List<FluxRecord> result = systemRepository.getUptimeByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}

}
