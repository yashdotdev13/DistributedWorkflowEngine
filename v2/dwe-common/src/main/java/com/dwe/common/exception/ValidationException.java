package com.dwe.common.exception;

/** Indicates that input data or a requested operation violates validation rules. */
public class ValidationException extends DomainException {

  public ValidationException(String message) {
    super(ErrorCode.VALIDATION_ERROR, message);
  }

  public ValidationException(String message, ErrorMetadata metadata) {
    super(ErrorCode.VALIDATION_ERROR, message, null, metadata);
  }

  public ValidationException(String message, Throwable cause) {
    super(ErrorCode.VALIDATION_ERROR, message, cause);
  }
}
