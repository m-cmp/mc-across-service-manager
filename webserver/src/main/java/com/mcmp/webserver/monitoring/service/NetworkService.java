package com.mcmp.webserver.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.influxdb.query.FluxRecord;
import com.mcmp.webserver.monitoring.repository.NetworkMonitoringRepository;

@Service
public class NetworkService {

	@Autowired
	private NetworkMonitoringRepository netRepository;

	/**
	 * 1개 인스턴스의 네트워크 인터페이스별 패킷 추이
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getNetPacketTransitionByHostName(String hostName) {
		List<FluxRecord> result = netRepository.getNetPacketTransitionByHostName(hostName);

		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());

		return values;
	}
	/**
	 * 1개 인스턴스의 TCP & UDP 추이
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getTCPUDPTransByHostName(String hostName) {
		List<FluxRecord> result = netRepository.getTCPUDPTransByHostName(hostName);
		
		// values값만 모은 List<Map<String, Object>> 생성
		List<Map<String, Object>> values = result.stream().map(record -> record.getValues())
				.collect(Collectors.toList());
		
		return values;
	}
}
