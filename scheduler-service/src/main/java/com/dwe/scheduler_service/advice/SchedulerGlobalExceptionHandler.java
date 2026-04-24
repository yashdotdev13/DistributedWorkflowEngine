package com.dwe.scheduler_service.advice;


import com.dwe.scheduler_service.exception.SchedulerBaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class SchedulerGlobalExceptionHandler {

    @ExceptionHandler(SchedulerBaseException.class)
    public ResponseEntity<?> handleSchedulerException(
            SchedulerBaseException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(
                        ex.getMessage(),
                        ex.getErrorCode(),
                        ex.getStatus().value(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex,
                                                    HttpServletRequest request) {

        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(
                        "Internal server error",
                        "INTERNAL_SERVER_ERROR",
                        500,
                        request.getRequestURI()
                ));
    }
}