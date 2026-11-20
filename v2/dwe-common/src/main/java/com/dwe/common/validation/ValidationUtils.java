package com.dwe.common.validation;

import java.util.Objects;

/** Provides reusable validation operations for common value constraints. */
public final class ValidationUtils {

  private ValidationUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Validates that a value is not null.
   *
   * @param value the value to validate
   * @param field the field name
   * @return the validation result
   */
  public static ValidationResult required(Object value, String field) {
    Objects.requireNonNull(field, "Field cannot be null");

    if (field.isBlank()) {
      throw new IllegalArgumentException("Field cannot be blank");
    }

    return value == null
        ? ValidationResult.invalid(new ValidationError(field, field + " is required"))
        : ValidationResult.valid();
  }

  /**
   * Validates that a string is not null, empty, or blank.
   *
   * @param value the string to validate
   * @param field the field name
   * @return the validation result
   */
  public static ValidationResult notBlank(String value, String field) {
    Objects.requireNonNull(field, "Field cannot be null");

    if (field.isBlank()) {
      throw new IllegalArgumentException("Field cannot be blank");
    }

    return value == null || value.isBlank()
        ? ValidationResult.invalid(new ValidationError(field, field + " must not be blank"))
        : ValidationResult.valid();
  }

  /**
   * Validates that a numeric value is positive.
   *
   * @param value the value to validate
   * @param field the field name
   * @return the validation result
   */
  public static ValidationResult positive(long value, String field) {
    Objects.requireNonNull(field, "Field cannot be null");

    if (field.isBlank()) {
      throw new IllegalArgumentException("Field cannot be blank");
    }

    return value <= 0
        ? ValidationResult.invalid(new ValidationError(field, field + " must be greater than zero"))
        : ValidationResult.valid();
  }

  /**
   * Validates that a numeric value is non-negative.
   *
   * @param value the value to validate
   * @param field the field name
   * @return the validation result
   */
  public static ValidationResult nonNegative(long value, String field) {
    Objects.requireNonNull(field, "Field cannot be null");

    if (field.isBlank()) {
      throw new IllegalArgumentException("Field cannot be blank");
    }

    return value < 0
        ? ValidationResult.invalid(new ValidationError(field, field + " must not be negative"))
        : ValidationResult.valid();
  }
}
