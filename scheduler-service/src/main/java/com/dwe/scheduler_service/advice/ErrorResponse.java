package com.dwe.scheduler_service.advice;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String message;
    private String errorCode;
    private int status;
    private String path;
    private LocalDateTime timestamp;
}
