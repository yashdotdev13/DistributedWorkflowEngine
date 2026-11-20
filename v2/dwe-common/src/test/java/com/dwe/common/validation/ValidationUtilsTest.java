package com.dwe.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  @Test
  void shouldAcceptNonNullRequiredValue() {
    assertTrue(ValidationUtils.required("workflow", "name").isValid());
  }

  @Test
  void shouldRejectNullRequiredValue() {
    ValidationResult result = ValidationUtils.required(null, "name");

    assertTrue(result.isInvalid());
    assertEquals("name", result.errors().get(0).field());
  }

  @Test
  void shouldAcceptNonBlankString() {
    assertTrue(ValidationUtils.notBlank("workflow", "name").isValid());
  }

  @Test
  void shouldRejectNullBlankAndEmptyStrings() {
    assertTrue(ValidationUtils.notBlank(null, "name").isInvalid());
    assertTrue(ValidationUtils.notBlank("", "name").isInvalid());
    assertTrue(ValidationUtils.notBlank("   ", "name").isInvalid());
  }

  @Test
  void shouldAcceptPositiveNumbers() {
    assertTrue(ValidationUtils.positive(1, "timeout").isValid());
    assertTrue(ValidationUtils.positive(Long.MAX_VALUE, "timeout").isValid());
  }

  @Test
  void shouldRejectZeroAndNegativeNumbersAsPositive() {
    assertTrue(ValidationUtils.positive(0, "timeout").isInvalid());
    assertTrue(ValidationUtils.positive(-1, "timeout").isInvalid());
  }

  @Test
  void shouldAcceptZeroAndPositiveNumbersAsNonNegative() {
    assertTrue(ValidationUtils.nonNegative(0, "retries").isValid());
    assertTrue(ValidationUtils.nonNegative(1, "retries").isValid());
  }

  @Test
  void shouldRejectNegativeNumbersAsNonNegative() {
    assertTrue(ValidationUtils.nonNegative(-1, "retries").isInvalid());
  }

  @Test
  void shouldRejectNullFieldName() {
    assertThrows(NullPointerException.class, () -> ValidationUtils.required("value", null));
  }

  @Test
  void shouldRejectBlankFieldName() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.notBlank("value", " "));
  }

  @Test
  void shouldRejectBlankFieldNameForNumericValidation() {
    assertThrows(IllegalArgumentException.class, () -> ValidationUtils.positive(1, " "));
  }
}
