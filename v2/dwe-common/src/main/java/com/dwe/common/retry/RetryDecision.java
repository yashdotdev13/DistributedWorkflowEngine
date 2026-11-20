package com.dwe.common.retry;

import java.time.Duration;
import java.util.Objects;

/**
 * Represents the outcome of evaluating whether a failed task attempt should be retried.
 *
 * @param shouldRetry whether another attempt should be scheduled
 * @param delay delay before the next retry
 * @param reason reason for the decision
 */
public record RetryDecision(boolean shouldRetry, Duration delay, Reason reason) {

  public RetryDecision {
    Objects.requireNonNull(delay, "Delay cannot be null");
    Objects.requireNonNull(reason, "Reason cannot be null");

    if (delay.isNegative()) {
      throw new IllegalArgumentException("Delay cannot be negative");
    }

    if (shouldRetry && reason != Reason.RETRY_SCHEDULED) {
      throw new IllegalArgumentException("A retry decision must have RETRY_SCHEDULED reason");
    }

    if (!shouldRetry && (delay.isPositive() || reason == Reason.RETRY_SCHEDULED)) {
      throw new IllegalArgumentException(
          "A rejected retry must have zero delay and a rejection reason");
    }
  }

  /**
   * Evaluates whether a failed attempt should be retried.
   *
   * @param policy retry configuration
   * @param classifier failure classifier
   * @param completedAttempts number of attempts already completed
   * @param failure failure from the completed attempt
   * @return retry decision
   */
  public static RetryDecision evaluate(
      RetryPolicy policy,
      RetryableErrorClassifier classifier,
      int completedAttempts,
      Throwable failure) {

    Objects.requireNonNull(policy, "Retry policy cannot be null");
    Objects.requireNonNull(classifier, "Error classifier cannot be null");
    Objects.requireNonNull(failure, "Failure cannot be null");

    if (completedAttempts < 1) {
      throw new IllegalArgumentException("Completed attempts must be at least 1");
    }

    if (!classifier.isRetryable(failure)) {
      return rejected(Reason.NON_RETRYABLE_FAILURE);
    }

    if (!policy.canRetry(completedAttempts)) {
      return rejected(Reason.RETRY_LIMIT_EXHAUSTED);
    }

    return new RetryDecision(
        true, policy.calculateDelay(completedAttempts), Reason.RETRY_SCHEDULED);
  }

  private static RetryDecision rejected(Reason reason) {
    return new RetryDecision(false, Duration.ZERO, reason);
  }

  public enum Reason {
    RETRY_SCHEDULED,
    NON_RETRYABLE_FAILURE,
    RETRY_LIMIT_EXHAUSTED
  }
}
