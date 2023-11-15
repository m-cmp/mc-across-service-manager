package com.mcmp.webserver.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mcmp.webserver.interceptor.ApiLoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	//타임아웃 설정 추가
	//Request, Response logging 추가
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(5))
				.interceptors(new ApiLoggingInterceptor())
				.build();
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("*")
		.allowedOrigins("http://localhost:5173",
				"http://127.0.0.1:5173", 
				"http://localhost:4173", 
				"http://127.0.0.1:4173",
				"http://34.64.91.20:4173",
				"http://34.64.91.20:5173");
	}
}
