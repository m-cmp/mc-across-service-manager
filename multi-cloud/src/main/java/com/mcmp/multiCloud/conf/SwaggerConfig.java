package com.mcmp.multiCloud.conf;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(title = "Multi Cloud API",
		description = "Multi Cloud Interface API 명세서",
		version = "v1.0.0"
		))
@Configuration
public class SwaggerConfig {

}
