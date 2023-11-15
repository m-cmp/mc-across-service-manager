package com.mcmp.webserver.common;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Schema(description = "에러 응답")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponse {
	
	@Schema(description = "API 서버", example = "BFF")
	private String from;
	@Schema(description = "HTTP 응답 코드", example = "400")
	private int code;
	@Schema(description = "HTTP 상태", example = "BAD_REQUEST")
	private HttpStatus status;
	@Schema(description = "응답 메시지", example = "에러 메시지")
	private String message;
}
