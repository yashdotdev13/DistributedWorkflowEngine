package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class InvalidTaskStateException extends WorkerBaseException {

    public InvalidTaskStateException(String taskId, String state) {
        super(
                "Invalid task state for execution. taskId=" + taskId + ", state=" + state,
                "INVALID_TASK_STATE",
                HttpStatus.BAD_REQUEST
        );
    }
}