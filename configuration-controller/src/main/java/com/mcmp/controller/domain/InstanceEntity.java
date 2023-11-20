package com.mcmp.controller.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Entity class for vm_instance
 *
 **/
@Entity
@Table(name = "tb_service_instance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceEntity {
    @Id
    @Column(name = "service_instance_id")
    private String serviceInstanceId;

    @Column(name = "across_service_id")
    private String acrossServiceId;

    @Column(name = "vm_instance_public_ip")
    private String publicIp;

    @Column(name = "agent_activate_yn")
    private String agentActivateYN;

    @Column(name = "agent_deploy_yn")
    private String agentDeployYN;

    @Column(name = "agent_deploy_date")
    private String agentDeployDate;

}
