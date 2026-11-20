package com.dwe.common.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the outcome of a validation operation.
 *
 * <p>A result with no errors is considered valid. A result containing one or more errors is
 * considered invalid.
 *
 * @param errors the validation errors
 */
public record ValidationResult(List<ValidationError> errors) {

  public ValidationResult {
    Objects.requireNonNull(errors, "Validation errors cannot be null");
    errors = List.copyOf(errors);
  }

  public static ValidationResult valid() {
    return new ValidationResult(List.of());
  }

  public static ValidationResult invalid(ValidationError error) {
    return new ValidationResult(
        List.of(Objects.requireNonNull(error, "Validation error cannot be null")));
  }

  public static ValidationResult invalid(List<ValidationError> errors) {
    Objects.requireNonNull(errors, "Validation errors cannot be null");

    if (errors.isEmpty()) {
      throw new IllegalArgumentException(
          "An invalid validation result must contain at least one error");
    }

    return new ValidationResult(errors);
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  public boolean isInvalid() {
    return !isValid();
  }

  /**
   * Combines this result with another validation result. Errors from both results are preserved in
   * their original order.
   *
   * @param other the result to combine with
   * @return the combined validation result
   */
  public ValidationResult combine(ValidationResult other) {
    Objects.requireNonNull(other, "Validation result cannot be null");

    List<ValidationError> combinedErrors = new ArrayList<>(errors);
    combinedErrors.addAll(other.errors());

    return new ValidationResult(combinedErrors);
  }
}
