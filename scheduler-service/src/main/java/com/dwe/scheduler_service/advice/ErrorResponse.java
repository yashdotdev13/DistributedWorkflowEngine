package com.dwe.scheduler_service.advice;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private String errorCode;
    private int status;
    private String path;
    private LocalDateTime timestamp;
}
