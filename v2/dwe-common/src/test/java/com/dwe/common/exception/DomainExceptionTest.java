package com.dwe.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DomainExceptionTest {

  @Test
  void shouldCreateExceptionWithErrorCodeAndMessage() {
    DomainException exception =
        new DomainException(ErrorCode.BUSINESS_RULE_VIOLATION, "Business rule violated");

    assertEquals(ErrorCode.BUSINESS_RULE_VIOLATION, exception.getErrorCode());
    assertEquals("Business rule violated", exception.getMessage());
    assertNotNull(exception.getMetadata());
    assertTrue(exception.getMetadata().details().isEmpty());
  }

  @Test
  void shouldCreateExceptionWithCause() {
    RuntimeException cause = new RuntimeException("Original failure");

    DomainException exception =
        new DomainException(ErrorCode.INTERNAL_ERROR, "Operation failed", cause);

    assertSame(cause, exception.getCause());
  }

  @Test
  void shouldCreateExceptionWithMetadata() {
    ErrorMetadata metadata = ErrorMetadata.of(Map.of("resourceId", "workflow-123"));

    DomainException exception =
        new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "Workflow not found", null, metadata);

    assertSame(metadata, exception.getMetadata());
    assertEquals("workflow-123", exception.getMetadata().details().get("resourceId"));
  }

  @Test
  void shouldRejectNullErrorCode() {
    assertThrows(
        NullPointerException.class, () -> new DomainException(null, "Something went wrong"));
  }

  @Test
  void shouldRejectNullMessage() {
    assertThrows(
        NullPointerException.class, () -> new DomainException(ErrorCode.INTERNAL_ERROR, null));
  }

  @Test
  void shouldRejectNullMetadata() {
    assertThrows(
        NullPointerException.class,
        () -> new DomainException(ErrorCode.INTERNAL_ERROR, "Failure", null, null));
  }

  @Test
  void shouldAllowNullCause() {
    DomainException exception = new DomainException(ErrorCode.INTERNAL_ERROR, "Failure");

    assertNull(exception.getCause());
  }
}
