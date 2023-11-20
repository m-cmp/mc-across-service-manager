package com.mcmp.webserver.web.instance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mcmp.webserver.web.instance.entity.ServiceInstance;

public interface InstanceRepository extends JpaRepository<ServiceInstance, String> {

	List<ServiceInstance> findAll();


}
