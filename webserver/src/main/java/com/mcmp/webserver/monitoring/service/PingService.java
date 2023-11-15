package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.PingMonitoringRepository;

@Service
public class PingService {

	@Autowired
	private PingMonitoringRepository repository;

	/**
	 * 연계서비스 ping 모니터링 정보 조회
	 * 
	 * @param hostArray
	 * @return
	 */
	public List<Map<String, Object>> getPingAvgTimeByHostNames(List<String> host) {
		List<FluxRecord> totalCPUFluxs = repository.getPingAvgTimeByHostNames(host);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = totalCPUFluxs.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
}
