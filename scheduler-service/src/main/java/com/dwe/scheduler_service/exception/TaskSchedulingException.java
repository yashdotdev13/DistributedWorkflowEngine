package com.dwe.scheduler_service.exception;

import org.springframework.http.HttpStatus;

public class TaskSchedulingException extends SchedulerBaseException {

    public TaskSchedulingException(String message) {
        super(
                message,
                "TASK_SCHEDULING_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}