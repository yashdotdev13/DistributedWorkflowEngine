package com.dwe.scheduler_service.exception;


import org.springframework.http.HttpStatus;

public class NoExecutableTaskException extends SchedulerBaseException {

    public NoExecutableTaskException(String workflowId) {
        super(
                "No executable tasks found for workflow: " + workflowId,
                "NO_EXECUTABLE_TASK",
                HttpStatus.OK
        );
    }
}