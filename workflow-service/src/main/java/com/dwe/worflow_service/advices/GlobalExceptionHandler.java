package com.dwe.worflow_service.advices;



import com.dwe.worflow_service.exceptions.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getStatus().value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                "Internal server error",
                "INTERNAL_SERVER_ERROR",
                500,
                request.getRequestURI()
        );

        return ResponseEntity.status(500).body(response);
    }
}