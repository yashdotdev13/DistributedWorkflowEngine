package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class UnsupportedTaskTypeException extends WorkerBaseException {

    public UnsupportedTaskTypeException(String type) {
        super(
                "Unsupported task type: " + type,
                "UNSUPPORTED_TASK_TYPE",
                HttpStatus.BAD_REQUEST
        );
    }
}