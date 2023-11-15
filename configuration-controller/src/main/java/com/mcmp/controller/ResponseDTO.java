package com.mcmp.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
public class ResponseDTO {

    private String message;
    private int status;
    private Object data;

    public ResponseDTO(HttpStatus status, Object data, String message){
        this.status = status.value();
        this.data = data;
        this.message = message;
    }


    @Override
    public String toString() {
        return "ResponseDto{" +
                "status=" + status +
                ", data=" + data +
                ", message=" + message +
                '}';
    }
}
