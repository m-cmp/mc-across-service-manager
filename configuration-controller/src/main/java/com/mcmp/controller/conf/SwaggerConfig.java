package com.mcmp.controller.conf;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(title = "Configuration & Controller API",
		description = "Conf/Controller API Documentation\n - Monitoring Service API\n - Application Service API\n - Migration Service API",
		version = "v1.0.0"
		))
@Configuration
public class SwaggerConfig {

}
