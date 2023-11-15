package com.mcmp.controller.exceptionHandler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Error response DTO
 *
 **/
@Data
@Builder
@Schema(description = "에러 응답")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    @Schema(description = "API 서버", example = "conf/controller")
    private String from;
    @Schema(description = "HTTP 응답 코드", example = "400")
    private int code;
    @Schema(description = "HTTP 상태", example = "BAD_REQUEST")
    private HttpStatus status;
    @Schema(description = "응답 메시지", example = "에러 메시지")
    private String message;
}
