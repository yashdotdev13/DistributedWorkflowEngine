package com.dwe.common.result;

import com.dwe.common.exception.ErrorCode;
import com.dwe.common.exception.ErrorMetadata;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents the outcome of an operation that can either succeed or fail.
 *
 * @param <T> the type of the successful result value
 */
public sealed interface Result<T> permits Result.Success, Result.Failure {

  /** Represents a successful operation. */
  record Success<V>(V resultValue) implements Result<V> {

    public Success {
      Objects.requireNonNull(resultValue, "Success value cannot be null");
    }
  }

  /** Represents a failed operation. */
  record Failure<V>(FailureDetails failureDetails) implements Result<V> {

    public Failure {
      Objects.requireNonNull(failureDetails, "Failure error cannot be null");
    }
  }

  /** Contains structured information about an operation failure. */
  record FailureDetails(ErrorCode code, String message, ErrorMetadata metadata) {

    public FailureDetails {
      Objects.requireNonNull(code, "Error code cannot be null");
      Objects.requireNonNull(message, "Error message cannot be null");
      Objects.requireNonNull(metadata, "Error metadata cannot be null");

      if (message.isBlank()) {
        throw new IllegalArgumentException("Error message cannot be blank");
      }
    }
  }

  static <V> Result<V> success(V value) {
    return new Success<>(value);
  }

  static <V> Result<V> failure(ErrorCode code, String message) {
    return failure(code, message, ErrorMetadata.empty());
  }

  static <V> Result<V> failure(ErrorCode code, String message, ErrorMetadata metadata) {
    return new Failure<>(new FailureDetails(code, message, metadata));
  }

  default boolean isSuccess() {
    return this instanceof Success<?>;
  }

  default boolean isFailure() {
    return this instanceof Failure<?>;
  }

  default Optional<T> value() {
    if (this instanceof Success<T> success) {
      return Optional.of(success.resultValue());
    }

    return Optional.empty();
  }

  default Optional<FailureDetails> error() {
    if (this instanceof Failure<T> failure) {
      return Optional.of(failure.failureDetails());
    }

    return Optional.empty();
  }

  default <U> Result<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "Mapper cannot be null");

    if (this instanceof Success<T> success) {
      return Result.success(mapper.apply(success.resultValue()));
    }

    return castFailure();
  }

  default <U> Result<U> flatMap(Function<? super T, ? extends Result<U>> mapper) {
    Objects.requireNonNull(mapper, "Mapper cannot be null");

    if (this instanceof Success<T> success) {
      return Objects.requireNonNull(
          mapper.apply(success.resultValue()), "Mapper result cannot be null");
    }

    return castFailure();
  }

  default T orElse(T fallback) {
    if (this instanceof Success<T> success) {
      return success.resultValue();
    }

    return fallback;
  }

  @SuppressWarnings("unchecked")
  private <U> Result<U> castFailure() {
    return (Result<U>) this;
  }
}
