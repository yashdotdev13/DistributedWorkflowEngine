package com.dwe.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationResultTest {

  @Test
  void shouldCreateValidResult() {
    ValidationResult result = ValidationResult.valid();

    assertTrue(result.isValid());
    assertFalse(result.isInvalid());
    assertTrue(result.errors().isEmpty());
  }

  @Test
  void shouldCreateInvalidResult() {
    ValidationError error = new ValidationError("name", "Name is required");

    ValidationResult result = ValidationResult.invalid(error);

    assertTrue(result.isInvalid());
    assertEquals(List.of(error), result.errors());
  }

  @Test
  void shouldCreateInvalidResultWithMultipleErrors() {
    List<ValidationError> errors =
        List.of(
            new ValidationError("name", "Name is required"),
            new ValidationError("description", "Description is required"));

    ValidationResult result = ValidationResult.invalid(errors);

    assertEquals(2, result.errors().size());
    assertEquals(errors, result.errors());
  }

  @Test
  void shouldRejectEmptyInvalidResult() {
    assertThrows(IllegalArgumentException.class, () -> ValidationResult.invalid(List.of()));
  }

  @Test
  void shouldRejectNullErrors() {
    assertThrows(NullPointerException.class, () -> new ValidationResult(null));
  }

  @Test
  void shouldDefensivelyCopyErrors() {
    List<ValidationError> errors = new ArrayList<>();
    errors.add(new ValidationError("name", "Name is required"));

    ValidationResult result = new ValidationResult(errors);
    errors.clear();

    assertEquals(1, result.errors().size());
  }

  @Test
  void shouldPreventModificationOfErrors() {
    ValidationResult result =
        ValidationResult.invalid(new ValidationError("name", "Name is required"));

    assertThrows(
        UnsupportedOperationException.class,
        () -> result.errors().add(new ValidationError("description", "Description is required")));
  }

  @Test
  void shouldCombineValidationResults() {
    ValidationResult first =
        ValidationResult.invalid(new ValidationError("name", "Name is required"));

    ValidationResult second =
        ValidationResult.invalid(new ValidationError("description", "Description is required"));

    ValidationResult combined = first.combine(second);

    assertEquals(2, combined.errors().size());
    assertEquals("name", combined.errors().get(0).field());
    assertEquals("description", combined.errors().get(1).field());
  }

  @Test
  void shouldCombineValidResults() {
    ValidationResult combined = ValidationResult.valid().combine(ValidationResult.valid());

    assertTrue(combined.isValid());
  }

  @Test
  void shouldRejectNullResultDuringCombination() {
    assertThrows(NullPointerException.class, () -> ValidationResult.valid().combine(null));
  }
}
