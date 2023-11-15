package com.mcmp.orchestrator.conf;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(title = "Service/app Orchestrator API",
		description = "SA Orchestrator API Documentation\n - Service instance info management API\n - Service instance API\n - Across-Serivce API",
		version = "v1.0.0"
		))
@Configuration
public class SwaggerConfig {

}
