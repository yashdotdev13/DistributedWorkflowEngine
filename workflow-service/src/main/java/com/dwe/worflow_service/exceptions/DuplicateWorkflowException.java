package com.dwe.worflow_service.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateWorkflowException extends BaseException{

    public DuplicateWorkflowException(String name) {
        super(
                "Workflow already exists with name: " + name,
                "WORKFLOW_ALREADY_EXISTS",
                HttpStatus.CONFLICT
        );
    }
}
