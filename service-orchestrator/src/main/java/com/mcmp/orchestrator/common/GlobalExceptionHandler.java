package com.mcmp.orchestrator.common;

import java.net.ConnectException;
import java.net.HttpRetryException;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.mcmp.orchestrator.exception.InvalidDataException;
import com.mcmp.orchestrator.exception.InvalidInputException;
import com.mcmp.orchestrator.util.OrchestratorConst;

import jakarta.persistence.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final String INVALID_REQUEST_PATH_VARIABLE = "API Pathvariable 값이 유효하지 않습니다.";		//400
	private static final String MISSING_REQUEST_PARAMETER = "API 요청 파라미터 값이 유효하지 않습니다.";				//400
	private static final String NOT_FOUND_404 = "잘못된 URI입니다.";											//404
	private static final String NOT_SUPPORTED_HTTP_METHOD_405 = "지원되지 않는 HTTP 메서드입니다.";					//405
	
	// TODO - 에러 리스폰스 맞추고 수정하기

	/**
	 * 사용자 정의를 제외한 모든 에러 처리
	 * INTERNAL_SERVER_ERROR
	 * @param e
	 * @return ResponseEntity<Object>
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleGenericException(Exception e) {
		log.error("Unexpected Exception occurred: {}", e.getMessage(), e);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
			.from(OrchestratorConst.ORCHESTRATOR)
			.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.message(e.getMessage())
			.build();
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	/**
	 * PathVariable Error
	 * RuntimeException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		String message = INVALID_REQUEST_PATH_VARIABLE + "["+ e.getName() + ": " + e.getValue() +"]";
		
		log.error("MethodArgumentTypeMismatchException occurred: {}", e.getMessage(), e);
		log.info(message);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(message)
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
	
	/**
	 * Request Parameter Error
	 * RuntimeException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		String message = MISSING_REQUEST_PARAMETER + "["+ e.getParameterName() +"]";
		
		log.error("MissingServletRequestParameterException occurred: {}", e.getMessage(), e);
		log.info(message);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(message)
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
	
	/**
	 * 404 에러 처리
	 * 잘못된 URI 호출
	 * @param e
	 * @return
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
		String message = NOT_FOUND_404 + "["+ e.getRequestURL() + "] ";
		
		log.error("NoHandlerFoundException occurred: {}", e.getMessage(), e);
        log.info(message);
        
        ApiErrorResponse response = ApiErrorResponse.builder()
        		.from(OrchestratorConst.ORCHESTRATOR)
        		.code(HttpStatus.NOT_FOUND.value())
				.status(HttpStatus.NOT_FOUND)
				.message(message)
				.build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
	
	/**
	 * 405 에러 처리
	 * 잘못된 HTTP Method 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		String message = NOT_SUPPORTED_HTTP_METHOD_405 + " [지원되는 메서드: "+ String.join(", ", e.getSupportedMethods()) +"]";
		
		log.error("HttpRequestMethodNotSupported Exception occurred: {}", e.getMessage(), e);
		log.info("현재 메서드는({})는 {}", e.getMethod(), message);
		ApiErrorResponse response = ApiErrorResponse.builder()
	       		.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.METHOD_NOT_ALLOWED.value())
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.message(message)
				.build();
		
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> exceptionHandler(NoSuchElementException e) {	
		
		ApiErrorResponse error = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
			
		return ResponseEntity.badRequest().body(error);
    }
	
	/**
	 * 400 에러 처리
	 * RuntimeException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
		log.error("RuntimeException occurred: {}", e.getMessage(), e);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
	
	/**
	 * 연결 관련 에러 처리
	 * ConnectException, HttpRetryException
	 * @param e
	 * @return
	 */
	@ExceptionHandler({ConnectException.class, HttpRetryException.class})
	public ResponseEntity<Object> exceptionHandler(Exception e) {	
		log.error("{} occurred: {}", e.getClass(), e.getMessage(), e);
		
		ApiErrorResponse error = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message("Check if airflow is alive.")
				.build();
		
		return ResponseEntity.internalServerError().body(error);
	}
	
	/**
	 * JPA 쿼리 관련 에러
	 * NonUniqueResultException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(NonUniqueResultException.class)
	public ResponseEntity<Object> exceptionHandler(NonUniqueResultException e) {	
		log.error("NonUniqueResultException occurred: {}", e.getMessage(), e);
		
		ApiErrorResponse error = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message("Something went wrong. Check with operator.")
				.build();
		
		return ResponseEntity.internalServerError().body(error);
	}
	
	/**
	 * 사용자 에러 처리
	 * InvalidInputException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<Object> exceptionHandler(InvalidInputException e) {	
		
		ApiErrorResponse error = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
			
		return ResponseEntity.badRequest().body(error);
	}
	
	/**
	 * 사용자 에러 처리
	 * InvalidDataException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<Object> exceptionHandler(InvalidDataException e) {	
		
		ApiErrorResponse error = ApiErrorResponse.builder()
				.from(OrchestratorConst.ORCHESTRATOR)
				.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(e.getMessage())
				.build();
			
		return ResponseEntity.badRequest().body(error);
	}
}
