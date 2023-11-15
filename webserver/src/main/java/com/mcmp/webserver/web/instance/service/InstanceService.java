package com.mcmp.webserver.web.instance.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.web.instance.dto.ServiceInstanceDTO;
import com.mcmp.webserver.web.instance.entity.ServiceInstance;
import com.mcmp.webserver.web.instance.repository.InstanceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인스턴스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InstanceService {
	@Autowired
	private InstanceRepository instanceRepositort;

	/**
	 * 인스턴스 전체 목록 조회
	 */
	public List<ServiceInstanceDTO> selectAll() {

		List<ServiceInstance> instanceList = instanceRepositort.findAll();
		log.info("[{}] {}", "selectAll", instanceList.toString());

		return instanceList.stream().map(ServiceInstanceDTO::of).collect(Collectors.toList());
	}


}
