package com.dwe.common.validation;

import java.util.Objects;

/**
 * Represents an individual validation failure.
 *
 * @param field the name of the field that failed validation
 * @param message a human-readable explanation of the failure
 */
public record ValidationError(String field, String message) {

  public ValidationError {
    Objects.requireNonNull(field, "Validation field cannot be null");
    Objects.requireNonNull(message, "Validation message cannot be null");

    if (field.isBlank()) {
      throw new IllegalArgumentException("Validation field cannot be blank");
    }

    if (message.isBlank()) {
      throw new IllegalArgumentException("Validation message cannot be blank");
    }
  }
}
