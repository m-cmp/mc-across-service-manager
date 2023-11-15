package com.mcmp.webserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Configuration
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "influxdb")
public class InfluxDBConfiguration {

	private String url;
	private String token;
	private String org;
	private String bucket;

	@Bean
	public InfluxDBClient influxDBClient() {
		return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
	}
}
