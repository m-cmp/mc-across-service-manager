package com.mcmp.controller.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author : Jihyeong Lee
 * @Project : mcmp-conf/controller
 * @version : 1.0.0
 * @date : 10/27/23
 * @class-description : Exception handler controller class
 *
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description :  Exception handler for runtime error
     *
     **/
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .from("conf/controller")
                .code(500)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        ResponseEntity response = ResponseEntity.internalServerError().body(errorResponse);
        return response;
    }

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description :  Exception handler for deployment error
     *
     **/
    @ExceptionHandler(DeploymentException.class)
    public ResponseEntity<ErrorResponse> handleDeploymentException(DeploymentException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .from("conf/controller")
                .code(500)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        ResponseEntity response = ResponseEntity.internalServerError().body(errorResponse);

        return response;
    }

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description :  Exception handler for dataUpdate error
     *
     **/
    @ExceptionHandler(DatabaseUpdateException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseUpdateException(DatabaseUpdateException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .from("conf/controller")
                .code(501)
                .status(HttpStatus.NOT_IMPLEMENTED)
                .message(e.getMessage())
                .build();
        ResponseEntity response = ResponseEntity.internalServerError().body(errorResponse);

        return response;
    }

    /**
     *
     * @date : 10/27/23
     * @author : Jihyeong Lee
     * @method-description : Exception handler for invalid parameter error
     *
     **/
    @ExceptionHandler(WrongParameterException.class)
    public ResponseEntity<ErrorResponse> wrongParameter(WrongParameterException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .from("conf/controller")
                .code(404)
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
        ResponseEntity response = ResponseEntity.internalServerError().body(errorResponse);

        return response;
    }

    /**
     *
     * @date : 10/29/23
     * @author : Jihyeong Lee
     * @method-description : exception handler for re-try deployment or activation application or monitoring-agent
     *
     **/
    @ExceptionHandler(DeployedException.class)
    public ResponseEntity<ErrorResponse> deployedException(DeployedException e) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .from("conf/controller")
                .code(404)
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
        ResponseEntity response = ResponseEntity.internalServerError().body(errorResponse);

        return response;
    }

    public static class WrongParameterException extends RuntimeException {

        public WrongParameterException(String cause) {
            super(cause);
        }
    }

    public static class DeployedException extends RuntimeException {

        public DeployedException(String cause) {
            super(cause);
        }
    }

    public static class DeploymentException extends RuntimeException {
        private String failedIp;

        public DeploymentException(String cause) {
            super(cause);
        }

        public String getFailedIp() {
            return this.failedIp;
        }
    }

    public static class DatabaseUpdateException extends RuntimeException {

        public DatabaseUpdateException(Throwable cause) {
            super(cause);
        }
    }

}
