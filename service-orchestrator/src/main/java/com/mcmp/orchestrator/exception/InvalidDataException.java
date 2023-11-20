package com.mcmp.orchestrator.exception;


/**
 * SA Orchestrator 내부 이슈로 유효하지 않은 데이터 exception
 * 
 * @details SA Orchestrator 내부 이슈로 유효하지 않은 데이터 exception 클래스
 * @author 오승재
 *
 */
@SuppressWarnings("serial")
public class InvalidDataException extends RuntimeException {

	public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }
}
