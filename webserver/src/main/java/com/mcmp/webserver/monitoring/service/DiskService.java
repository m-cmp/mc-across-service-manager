package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.DiskMonitoringRepository;

@Service
public class DiskService {

	@Autowired
	private DiskMonitoringRepository diskRepository;

	public List<Map<String, Object>> getDiskUsed() {

		List<FluxRecord> disk = diskRepository.getDiskUsed();

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = disk.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}

	public List<Map<String, Object>> getDiskUsedByHostName(String hostName) {
		List<FluxRecord> disk = diskRepository.getDiskUsedByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = disk.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
}
