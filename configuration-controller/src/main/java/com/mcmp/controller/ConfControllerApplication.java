package com.mcmp.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableJpaAuditing
@SpringBootApplication
public class ConfControllerApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ConfControllerApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
