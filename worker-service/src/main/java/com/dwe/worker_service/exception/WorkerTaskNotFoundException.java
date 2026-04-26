package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class WorkerTaskNotFoundException extends WorkerBaseException {

    public WorkerTaskNotFoundException(String taskId) {
        super(
                "Task not found during execution: " + taskId,
                "TASK_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}