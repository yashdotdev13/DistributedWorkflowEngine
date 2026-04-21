package com.dwe.worflow_service.exceptions;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends BaseException {

    public TaskNotFoundException(String taskId) {
        super(
                "Task not found with id: " + taskId,
                "TASK_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
