package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class InvalidTaskConfigException extends WorkerBaseException {

    public InvalidTaskConfigException(String taskId) {
        super(
                "Invalid configuration for taskId=" + taskId,
                "INVALID_TASK_CONFIG",
                HttpStatus.BAD_REQUEST
        );
    }
}