package com.dwe.common.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class RetryDecisionTest {

  private final RetryPolicy policy =
      new RetryPolicy(3, Duration.ofSeconds(2), 2.0, Duration.ofSeconds(30));

  @Test
  void shouldScheduleRetryForRetryableFailure() {
    RetryDecision decision =
        RetryDecision.evaluate(
            policy,
            RetryableErrorClassifier.alwaysRetryable(),
            1,
            new RuntimeException("Temporary failure"));

    assertTrue(decision.shouldRetry());
    assertEquals(Duration.ofSeconds(2), decision.delay());
    assertEquals(RetryDecision.Reason.RETRY_SCHEDULED, decision.reason());
  }

  @Test
  void shouldCalculateBackoffForSubsequentRetry() {
    RetryDecision decision =
        RetryDecision.evaluate(
            policy,
            RetryableErrorClassifier.alwaysRetryable(),
            2,
            new RuntimeException("Temporary failure"));

    assertTrue(decision.shouldRetry());
    assertEquals(Duration.ofSeconds(4), decision.delay());
  }

  @Test
  void shouldRejectNonRetryableFailure() {
    RetryDecision decision =
        RetryDecision.evaluate(
            policy,
            RetryableErrorClassifier.neverRetryable(),
            1,
            new IllegalArgumentException("Invalid input"));

    assertFalse(decision.shouldRetry());
    assertEquals(Duration.ZERO, decision.delay());
    assertEquals(RetryDecision.Reason.NON_RETRYABLE_FAILURE, decision.reason());
  }

  @Test
  void shouldRejectRetryWhenRetryLimitIsExhausted() {
    RetryDecision decision =
        RetryDecision.evaluate(
            policy,
            RetryableErrorClassifier.alwaysRetryable(),
            4,
            new RuntimeException("Temporary failure"));

    assertFalse(decision.shouldRetry());
    assertEquals(Duration.ZERO, decision.delay());
    assertEquals(RetryDecision.Reason.RETRY_LIMIT_EXHAUSTED, decision.reason());
  }

  @Test
  void shouldPrioritizeNonRetryableFailureOverRetryLimit() {
    RetryDecision decision =
        RetryDecision.evaluate(
            policy,
            RetryableErrorClassifier.neverRetryable(),
            4,
            new IllegalArgumentException("Invalid input"));

    assertEquals(RetryDecision.Reason.NON_RETRYABLE_FAILURE, decision.reason());
  }

  @Test
  void shouldRejectZeroCompletedAttempts() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            RetryDecision.evaluate(
                policy, RetryableErrorClassifier.alwaysRetryable(), 0, new RuntimeException()));
  }

  @Test
  void shouldRejectNegativeCompletedAttempts() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            RetryDecision.evaluate(
                policy, RetryableErrorClassifier.alwaysRetryable(), -1, new RuntimeException()));
  }

  @Test
  void shouldRejectNullPolicy() {
    assertThrows(
        NullPointerException.class,
        () ->
            RetryDecision.evaluate(
                null, RetryableErrorClassifier.alwaysRetryable(), 1, new RuntimeException()));
  }

  @Test
  void shouldRejectNullClassifier() {
    assertThrows(
        NullPointerException.class,
        () -> RetryDecision.evaluate(policy, null, 1, new RuntimeException()));
  }

  @Test
  void shouldRejectNullFailure() {
    assertThrows(
        NullPointerException.class,
        () -> RetryDecision.evaluate(policy, RetryableErrorClassifier.alwaysRetryable(), 1, null));
  }
}
