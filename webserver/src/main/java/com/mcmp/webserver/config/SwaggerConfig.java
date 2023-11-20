package com.mcmp.webserver.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(title = "M-CMP mc-across-service-manager webserber API",
		description = "mc-across-service-manager API 목록\n - 연계 서비스 API\n - 서비스 인스턴스 API\n - 서비스 템플릿 API",
		version = "v1.0.0"
))
@Configuration
public class SwaggerConfig {

}