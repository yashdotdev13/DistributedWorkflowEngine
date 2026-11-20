package com.dwe.common.exception;

/** Indicates that a requested domain resource could not be found. */
public class ResourceNotFoundException extends DomainException {

  public ResourceNotFoundException(String message) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message);
  }

  public ResourceNotFoundException(String message, ErrorMetadata metadata) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message, null, metadata);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message, cause);
  }
}
