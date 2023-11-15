package com.mcmp.multiCloud.common;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Schema(description = "공통 응답")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiCommonResponse<T> {
	
		@Schema(description = "HTTP 응답 코드", example = "200")
		private int code;
		@Schema(description = "HTTP 상태", example = "OK")
		private HttpStatus status;
		@Schema(description = "응답 메시지", example = "요청 처리 메시지")
		private String message;
		private T data;

}
