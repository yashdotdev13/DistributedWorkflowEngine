package com.dwe.common.validation;

/**
 * Defines a contract for validating objects of a specific type.
 *
 * @param <T> the type of object to validate
 */
@FunctionalInterface
public interface Validator<T> {

  /**
   * Validates the supplied object.
   *
   * @param target the object to validate
   * @return the validation result
   */
  ValidationResult validate(T target);

  /**
   * Combines this validator with another validator.
   *
   * <p>Both validators are executed, and their validation errors are combined.
   *
   * @param other the validator to combine with
   * @return a combined validator
   */
  default Validator<T> and(Validator<? super T> other) {
    java.util.Objects.requireNonNull(other, "Validator cannot be null");

    return target -> validate(target).combine(other.validate(target));
  }

  /**
   * Creates a validator that always succeeds.
   *
   * @param <T> the type of object to validate
   * @return a successful validator
   */
  static <T> Validator<T> alwaysValid() {
    return target -> ValidationResult.valid();
  }
}
