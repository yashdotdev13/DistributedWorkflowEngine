package com.dwe.scheduler_service.exception;


import org.springframework.http.HttpStatus;

public class SchedulerWorkflowNotFoundException extends SchedulerBaseException {

    public SchedulerWorkflowNotFoundException(String workflowId) {
        super(
                "Workflow not found during scheduling: " + workflowId,
                "SCHEDULER_WORKFLOW_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}