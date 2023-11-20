package com.mcmp.webserver.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidException extends RuntimeException {
	
	private static final long serialVersionUID = 1033155778945371472L;
	
	public InvalidException(String message) {
		super(message);
	}
}
