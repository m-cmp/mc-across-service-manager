package com.mcmp.orchestrator.exception;


/**
 * SA Orchestrator 유효하지 않은 input exception
 * 
 * @details SA Orchestrator 유효하지 않은 input exception 클래스
 * @author 오승재
 *
 */
@SuppressWarnings("serial")
public class InvalidInputException extends RuntimeException {

	public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
