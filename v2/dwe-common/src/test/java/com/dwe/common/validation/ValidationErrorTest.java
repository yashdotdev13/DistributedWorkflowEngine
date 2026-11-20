package com.dwe.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValidationErrorTest {

  @Test
  void shouldCreateValidationError() {
    ValidationError error = new ValidationError("workflowName", "Name is required");

    assertEquals("workflowName", error.field());
    assertEquals("Name is required", error.message());
  }

  @Test
  void shouldRejectNullField() {
    assertThrows(NullPointerException.class, () -> new ValidationError(null, "Name is required"));
  }

  @Test
  void shouldRejectNullMessage() {
    assertThrows(NullPointerException.class, () -> new ValidationError("workflowName", null));
  }

  @Test
  void shouldRejectBlankField() {
    assertThrows(
        IllegalArgumentException.class, () -> new ValidationError(" ", "Name is required"));
  }

  @Test
  void shouldRejectBlankMessage() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationError("workflowName", " "));
  }
}
