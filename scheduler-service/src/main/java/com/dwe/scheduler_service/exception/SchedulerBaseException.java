package com.dwe.scheduler_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SchedulerBaseException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public SchedulerBaseException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
