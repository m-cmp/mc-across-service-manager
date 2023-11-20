package com.mcmp.webserver.web.vpc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmp.webserver.web.vpc.dto.VPCDTO;
import com.mcmp.webserver.web.vpc.entity.VPC;
import com.mcmp.webserver.web.vpc.repository.VPCRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VPCService {

	@Autowired
	public VPCRepository vpcRepository;

	public List<VPCDTO> selectAll() {
		log.debug("[{}] {}", "VPCService", "selectAll");
		
		List<VPC> vpcList = vpcRepository.findAll();
		log.debug("[{}] {}", "selectAll", vpcList.toString());
		
		return vpcList.stream().map(VPCDTO::of).collect(Collectors.toList());
	}
}
