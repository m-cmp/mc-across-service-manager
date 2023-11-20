package com.mcmp.multiCloud.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;


public class ApiLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		//API 호출 전
		requestLog(request, body);
		
		//API 실행
		ClientHttpResponse response = execution.execute(request, body);
		
		//API 호출 후
		responseLog(response);
		
		return response;
	}
	
	private void requestLog(HttpRequest request, byte[] body) throws IOException {
        log.info("URI: {}, Method: {}, Headers:{}, Body:{} ", request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

	private void responseLog(ClientHttpResponse response) throws IOException {
        String body = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));

        log.info("Status: {}, Headers:{}, Body:{} ", response.getStatusCode(), response.getHeaders(), body);
    }
}
