package com.dwe.worflow_service.exceptions;

import org.springframework.http.HttpStatus;

public class WorkflowNotFoundException extends BaseException {
    public WorkflowNotFoundException(String workflowId) {
        super(
                "Workflow not found with id: " + workflowId,
                "WORKFLOW_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
