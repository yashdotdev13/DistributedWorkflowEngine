package com.dwe.scheduler_service.exception;


import org.springframework.http.HttpStatus;

public class InvalidWorkflowStateException extends SchedulerBaseException {

    public InvalidWorkflowStateException(String workflowId, String state) {
        super(
                "Invalid workflow state for scheduling. workflowId=" + workflowId + ", state=" + state,
                "INVALID_WORKFLOW_STATE",
                HttpStatus.BAD_REQUEST
        );
    }
}