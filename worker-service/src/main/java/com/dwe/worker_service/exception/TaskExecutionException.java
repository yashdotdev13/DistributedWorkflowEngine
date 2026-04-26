package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class TaskExecutionException extends WorkerBaseException {

    public TaskExecutionException(String taskId, String reason) {
        super(
                "Task execution failed. taskId=" + taskId + ", reason=" + reason,
                "TASK_EXECUTION_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}