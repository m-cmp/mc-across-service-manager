package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.CPUMonitoringRepository;

@Service
public class CPUService {

	@Autowired
	private CPUMonitoringRepository repository;

	public List<Map<String, Object>> getTotalCPUUsage() {
		List<FluxRecord> totalCPUFluxs = repository.getTotalCPU();

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = totalCPUFluxs.stream()
		  .map(record -> record.getValues())
		  .collect(Collectors.toList());
		
		return values;
	}
	
	public List<Map<String, Object>> getCPUUsageByHostName(String hostName) {
		List<FluxRecord> result = repository.getCPUUsageByHostName(hostName);
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream()
		  .map(record -> record.getValues())
		  .collect(Collectors.toList());
		
		return values;
	}
	
	public  List<Map<String, Object>> getCPUUsedByHostName (String hostName) {
		List<FluxRecord> result = repository.getCPUUsedByHostName(hostName);
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream()
		  .map(record -> record.getValues())
		  .collect(Collectors.toList());
		
		return values;
	}
}
