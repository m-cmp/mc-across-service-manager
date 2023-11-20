package com.mcmp.orchestrator.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * ApiLoggingInterceptor
 * 
 * @details API request 및 response 로그 출력용 인터셉터 클래스
 * @author 정현진
 *
 */
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
        log.info("\n - URI: {},\n - Method: {},\n - Headers:{},\n - Body:{} ", request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

	private void responseLog(ClientHttpResponse response) throws IOException {
        log.info("\n - Status: {},\n - Headers:{},\n - Body:{} ", response.getStatusCode(), response.getHeaders(), response.getBody());
    }
}
