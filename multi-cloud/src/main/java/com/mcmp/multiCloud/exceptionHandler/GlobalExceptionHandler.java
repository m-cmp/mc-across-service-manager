package com.mcmp.multiCloud.exceptionHandler;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.api.gax.rpc.NotFoundException;
import com.mcmp.multiCloud.common.ErrorResponse;
import com.mcmp.multiCloud.exception.CommandManagerException;
import com.mcmp.multiCloud.exception.DbNotFoundException;
import com.mcmp.multiCloud.exception.FileManagerException;
import com.mcmp.multiCloud.exception.InvalidInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private final static String MultiCloud = "MultiCloud";
	
	@ExceptionHandler(DbNotFoundException.class)
	public ResponseEntity<Object> exceptionHandler(DbNotFoundException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;
	}
	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<Object> exceptionHandler(InvalidInputException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;

	}
	
	@ExceptionHandler(FileManagerException.class)
	public ResponseEntity<Object> exceptionHandler(FileManagerException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;

	}
	
	@ExceptionHandler(CommandManagerException.class)
	public ResponseEntity<Object> exceptionHandler(CommandManagerException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;

	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> exceptionHandler(NotFoundException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;

	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> exceptionHandler(NoSuchElementException e) {
		ErrorResponse error = ErrorResponse.builder()
				.from(MultiCloud)
				.code(HttpStatus.BAD_REQUEST.value())
				.status(HttpStatus.BAD_REQUEST)
				.message(e.getMessage())
				.build();
		ResponseEntity<Object> response = ResponseEntity.badRequest().body(error);
			
		return response;

	}

}
