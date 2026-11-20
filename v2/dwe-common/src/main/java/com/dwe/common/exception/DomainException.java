package com.dwe.common.exception;

import java.util.Objects;

/** Base exception for domain and application-level failures. */
public class DomainException extends RuntimeException {

  private final ErrorCode errorCode;
  private final ErrorMetadata metadata;

  public DomainException(ErrorCode errorCode, String message) {
    this(errorCode, message, null, ErrorMetadata.empty());
  }

  public DomainException(ErrorCode errorCode, String message, Throwable cause) {
    this(errorCode, message, cause, ErrorMetadata.empty());
  }

  public DomainException(
      ErrorCode errorCode, String message, Throwable cause, ErrorMetadata metadata) {
    super(Objects.requireNonNull(message, "Exception message cannot be null"), cause);
    this.errorCode = Objects.requireNonNull(errorCode, "Error code cannot be null");
    this.metadata = Objects.requireNonNull(metadata, "Error metadata cannot be null");
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public ErrorMetadata getMetadata() {
    return metadata;
  }
}
