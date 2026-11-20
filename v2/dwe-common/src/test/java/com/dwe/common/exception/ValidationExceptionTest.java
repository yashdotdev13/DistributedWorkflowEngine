package com.dwe.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ValidationExceptionTest {

  @Test
  void shouldUseValidationErrorCode() {
    ValidationException exception = new ValidationException("Invalid workflow definition");

    assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
    assertEquals("Invalid workflow definition", exception.getMessage());
  }

  @Test
  void shouldAcceptErrorMetadata() {
    ErrorMetadata metadata =
        ErrorMetadata.of(Map.of("field", "workflowName", "reason", "must not be blank"));

    ValidationException exception =
        new ValidationException("Invalid workflow definition", metadata);

    assertSame(metadata, exception.getMetadata());
    assertEquals("workflowName", exception.getMetadata().details().get("field"));
    assertEquals("must not be blank", exception.getMetadata().details().get("reason"));
  }

  @Test
  void shouldAcceptExceptionCause() {
    IllegalArgumentException cause = new IllegalArgumentException("Invalid input");

    ValidationException exception = new ValidationException("Validation failed", cause);

    assertSame(cause, exception.getCause());
  }

  @Test
  void shouldProvideEmptyMetadataByDefault() {
    ValidationException exception = new ValidationException("Invalid input");

    assertTrue(exception.getMetadata().details().isEmpty());
  }
}
