package com.mcmp.controller.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Entity class for application
 *
 **/
@Entity
@Table(name = "tb_application")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppEntity {

    @Id
    @Column(name = "application_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String applicationId;

    @Column(name = "service_instance_id")
    private String serviceInstanceId;

    @Column(name = "application_name")
    private String applicationName;

    @Column(name = "application_type")
    private String applicationType;

    @Column(name = "application_activate_yn")
    private String applicationActivateYN;

    @Column(name = "application_create_date")
    private String applicationCreateDate;

}
