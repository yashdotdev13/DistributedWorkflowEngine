package com.dwe.worker_service.advices;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private String message;
    private String errorCode;
    private int status;
    private String path;
    private LocalDateTime timestamp;
}