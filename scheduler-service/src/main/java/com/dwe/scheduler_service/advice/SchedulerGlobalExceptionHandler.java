package com.dwe.scheduler_service.advice;



import com.dwe.scheduler_service.exception.SchedulerBaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class SchedulerGlobalExceptionHandler {

    @ExceptionHandler(SchedulerBaseException.class)
    public ResponseEntity<ErrorResponse> handleSchedulerException(
            SchedulerBaseException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .status(ex.getStatus().value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .message("Internal server error")
                .errorCode("INTERNAL_SERVER_ERROR")
                .status(500)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(500).body(response);
    }
}