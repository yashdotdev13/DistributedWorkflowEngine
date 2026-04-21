package com.dwe.worflow_service.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDAGException extends BaseException{

    public InvalidDAGException(String message) {
        super(
                message,
                "INVALID_DAG",
                HttpStatus.BAD_REQUEST
        );
    }
}
