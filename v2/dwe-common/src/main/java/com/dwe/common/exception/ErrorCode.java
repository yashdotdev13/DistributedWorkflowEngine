package com.dwe.common.exception;

/** Defines stable, machine-readable error codes used across the application. */
public enum ErrorCode {
  VALIDATION_ERROR,
  RESOURCE_NOT_FOUND,
  RESOURCE_ALREADY_EXISTS,
  BUSINESS_RULE_VIOLATION,
  OPERATION_NOT_ALLOWED,
  CONCURRENCY_CONFLICT,
  INTERNAL_ERROR
}
