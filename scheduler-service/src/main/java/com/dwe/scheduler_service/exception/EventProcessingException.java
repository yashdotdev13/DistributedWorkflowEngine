package com.dwe.scheduler_service.exception;


import org.springframework.http.HttpStatus;

public class EventProcessingException extends SchedulerBaseException {

    public EventProcessingException(String message) {
        super(
                message,
                "EVENT_PROCESSING_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}