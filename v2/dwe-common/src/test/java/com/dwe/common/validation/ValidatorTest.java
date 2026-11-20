package com.dwe.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidatorTest {

  @Test
  void shouldValidateUsingLambda() {
    Validator<String> validator =
        value ->
            value == null
                ? ValidationResult.invalid(new ValidationError("value", "Value is required"))
                : ValidationResult.valid();

    assertTrue(validator.validate("workflow").isValid());
    assertTrue(validator.validate(null).isInvalid());
  }

  @Test
  void shouldCreateAlwaysValidValidator() {
    Validator<String> validator = Validator.alwaysValid();

    assertNotNull(validator);
    assertTrue(validator.validate("anything").isValid());
    assertTrue(validator.validate(null).isValid());
  }

  @Test
  void shouldCombineValidators() {
    Validator<String> notNull =
        value ->
            value == null
                ? ValidationResult.invalid(new ValidationError("value", "Value is required"))
                : ValidationResult.valid();

    Validator<String> notBlank =
        value ->
            value != null && value.isBlank()
                ? ValidationResult.invalid(new ValidationError("value", "Value must not be blank"))
                : ValidationResult.valid();

    ValidationResult result = notNull.and(notBlank).validate("");

    assertTrue(result.isInvalid());
    assertEquals(1, result.errors().size());
    assertEquals("Value must not be blank", result.errors().get(0).message());
  }

  @Test
  void shouldAccumulateErrorsFromBothValidators() {
    Validator<String> first =
        value -> ValidationResult.invalid(new ValidationError("first", "First validation failed"));

    Validator<String> second =
        value ->
            ValidationResult.invalid(new ValidationError("second", "Second validation failed"));

    ValidationResult result = first.and(second).validate("value");

    assertEquals(2, result.errors().size());
    assertEquals("first", result.errors().get(0).field());
    assertEquals("second", result.errors().get(1).field());
  }

  @Test
  void shouldRejectNullValidatorDuringComposition() {
    Validator<String> validator = Validator.alwaysValid();

    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class, () -> validator.and(null));
  }
}
