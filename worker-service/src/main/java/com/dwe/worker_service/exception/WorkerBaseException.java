package com.dwe.worker_service.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WorkerBaseException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public WorkerBaseException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}