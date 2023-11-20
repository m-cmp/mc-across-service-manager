package com.mcmp.multiCloud.conf;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CustomConfigurer implements WebMvcConfigurer  {
	
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		
		return restTemplateBuilder
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(5))
                .build();
	}
}
