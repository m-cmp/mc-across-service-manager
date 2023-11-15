package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.MemoryMonitoringRepository;

@Service
public class MemoryService {

	@Autowired
	private MemoryMonitoringRepository memRepository;
	
	/**
	 * 인스턴스별 메모리 사용량 (%)
	 * @return
	 */
	public List<Map<String, Object>> getMemoryUsed() {
		List<FluxRecord> usedMem = memRepository.getMemoryUsed();

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = usedMem.stream()
		  .map(record -> record.getValues())
		  .collect(Collectors.toList());
		
		return values;
	}
	
	/**
	 * 1개 인스턴스의 메모리 사용량 (%)
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getMemoryUsedByHostName(String hostName) {
		List<FluxRecord> usedMem = memRepository.getMemoryUsedByHostName(hostName);

		List<Map<String, Object>> values = usedMem.stream()
		  .map(record -> record.getValues())
		  .collect(Collectors.toList());
		
		return values;
	}
	
	/**
	 * 1개 인스턴스의 상세 메모리 사용 모니터링
	 * 
	 * @param hostName
	 * @return
	 */
	public List<Map<String, Object>> getMemoryResourceByHostName(String hostName) {
		List<FluxRecord> result = memRepository.getMemoryResourceByHostName(hostName);
		
		List<Map<String, Object>> values = result.stream()
				.map(record -> record.getValues())
				.collect(Collectors.toList());
		
		return values;
	}
	
	
}
