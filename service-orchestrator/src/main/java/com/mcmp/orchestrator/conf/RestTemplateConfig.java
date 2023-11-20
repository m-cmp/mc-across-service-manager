package com.mcmp.orchestrator.conf;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.mcmp.orchestrator.interceptor.ApiLoggingInterceptor;

@Configuration 
public class RestTemplateConfig {
	
	@Value("${rest-api.airflow.url}")
	private String airflowRestUrl;
	@Value("${rest-api.airflow.username}")
	private String airflowUserName;
	@Value("${rest-api.airflow.password}")
	private String airflowPassword;
	
	
	@Bean 
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
			
		return restTemplateBuilder
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.basicAuthentication(airflowUserName, airflowPassword)
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(5))
				.uriTemplateHandler(new DefaultUriBuilderFactory(airflowRestUrl))
				.interceptors(new ApiLoggingInterceptor())
                .build();
	}
}
