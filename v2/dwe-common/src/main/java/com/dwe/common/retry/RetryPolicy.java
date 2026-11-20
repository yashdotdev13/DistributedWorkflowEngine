package com.dwe.common.retry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Objects;

/**
 * Defines the retry configuration for a task.
 *
 * @param maxRetries maximum number of retries after the initial attempt
 * @param initialDelay delay before the first retry
 * @param backoffMultiplier multiplier applied to the retry delay
 * @param maximumBackoff maximum permitted retry delay
 */
public record RetryPolicy(
    int maxRetries, Duration initialDelay, double backoffMultiplier, Duration maximumBackoff) {

  public RetryPolicy {
    if (maxRetries < 0) {
      throw new IllegalArgumentException("Maximum retries cannot be negative");
    }

    Objects.requireNonNull(initialDelay, "Initial delay cannot be null");
    Objects.requireNonNull(maximumBackoff, "Maximum backoff cannot be null");

    if (initialDelay.isNegative() || initialDelay.isZero()) {
      throw new IllegalArgumentException("Initial delay must be positive");
    }

    if (maximumBackoff.compareTo(initialDelay) < 0) {
      throw new IllegalArgumentException("Maximum backoff cannot be less than initial delay");
    }

    if (!Double.isFinite(backoffMultiplier) || backoffMultiplier < 1.0) {
      throw new IllegalArgumentException("Backoff multiplier must be finite and at least 1.0");
    }
  }

  /**
   * Indicates whether another retry is permitted.
   *
   * @param completedAttempts number of attempts already completed
   * @return true if another retry is permitted
   */
  public boolean canRetry(int completedAttempts) {
    if (completedAttempts < 1) {
      throw new IllegalArgumentException("Completed attempts must be at least 1");
    }

    return completedAttempts <= maxRetries;
  }

  /**
   * Calculates the delay before the next retry using exponential backoff.
   *
   * @param completedAttempts number of attempts already completed
   * @return calculated retry delay, capped at maximumBackoff
   */
  public Duration calculateDelay(int completedAttempts) {
    if (completedAttempts < 1) {
      throw new IllegalArgumentException("Completed attempts must be at least 1");
    }

    if (completedAttempts == 1 || backoffMultiplier == 1.0) {
      return initialDelay;
    }

    double multiplierPower = Math.pow(backoffMultiplier, completedAttempts - 1);

    if (!Double.isFinite(multiplierPower)) {
      return maximumBackoff;
    }

    BigDecimal billion = BigDecimal.valueOf(1_000_000_000L);

    BigDecimal initialNanos =
        BigDecimal.valueOf(initialDelay.getSeconds())
            .multiply(billion)
            .add(BigDecimal.valueOf(initialDelay.getNano()));

    BigDecimal maximumNanos =
        BigDecimal.valueOf(maximumBackoff.getSeconds())
            .multiply(billion)
            .add(BigDecimal.valueOf(maximumBackoff.getNano()));

    BigDecimal calculatedNanos = initialNanos.multiply(BigDecimal.valueOf(multiplierPower));

    if (calculatedNanos.compareTo(maximumNanos) >= 0) {
      return maximumBackoff;
    }

    BigDecimal roundedNanos = calculatedNanos.setScale(0, RoundingMode.HALF_UP);

    BigDecimal[] secondsAndNanos = roundedNanos.divideAndRemainder(billion);

    return Duration.ofSeconds(
        secondsAndNanos[0].longValueExact(), secondsAndNanos[1].longValueExact());
  }
}
