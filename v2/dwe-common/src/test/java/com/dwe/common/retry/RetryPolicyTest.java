package com.dwe.common.retry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class RetryPolicyTest {

  @Test
  void shouldCreateValidRetryPolicy() {
    assertDoesNotThrow(
        () -> new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectNegativeMaxRetries() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(-1, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectNullInitialDelay() {
    assertThrows(
        NullPointerException.class, () -> new RetryPolicy(3, null, 2.0, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectNullMaximumBackoff() {
    assertThrows(
        NullPointerException.class, () -> new RetryPolicy(3, Duration.ofSeconds(1), 2.0, null));
  }

  @Test
  void shouldRejectZeroInitialDelay() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(3, Duration.ZERO, 2.0, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectNegativeInitialDelay() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(3, Duration.ofSeconds(-1), 2.0, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectMaximumBackoffLessThanInitialDelay() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(3, Duration.ofSeconds(10), 2.0, Duration.ofSeconds(5)));
  }

  @Test
  void shouldRejectBackoffMultiplierLessThanOne() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryPolicy(3, Duration.ofSeconds(1), 0.5, Duration.ofSeconds(30)));
  }

  @Test
  void shouldRejectNonFiniteBackoffMultiplier() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new RetryPolicy(
                3, Duration.ofSeconds(1), Double.POSITIVE_INFINITY, Duration.ofSeconds(30)));
  }

  @Test
  void shouldAllowRetryWhenRetriesRemain() {
    RetryPolicy policy = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertTrue(policy.canRetry(1));
    assertTrue(policy.canRetry(2));
  }

  @Test
  void shouldRejectRetryWhenRetryLimitIsExhausted() {
    RetryPolicy policy = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertFalse(policy.canRetry(3));
  }

  @Test
  void shouldRejectRetryWhenNoRetriesAreConfigured() {
    RetryPolicy policy = new RetryPolicy(0, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertFalse(policy.canRetry(1));
  }

  @Test
  void shouldRejectZeroCompletedAttempts() {
    RetryPolicy policy = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertThrows(IllegalArgumentException.class, () -> policy.canRetry(0));
  }

  @Test
  void shouldRejectNegativeCompletedAttempts() {
    RetryPolicy policy = new RetryPolicy(2, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertThrows(IllegalArgumentException.class, () -> policy.canRetry(-1));
  }

  @Test
  void shouldReturnInitialDelayForFirstRetry() {
    RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(2), 2.0, Duration.ofSeconds(30));

    assertEquals(Duration.ofSeconds(2), policy.calculateDelay(1));
  }

  @Test
  void shouldCalculateExponentialBackoff() {
    RetryPolicy policy = new RetryPolicy(5, Duration.ofSeconds(2), 2.0, Duration.ofSeconds(60));

    assertEquals(Duration.ofSeconds(2), policy.calculateDelay(1));
    assertEquals(Duration.ofSeconds(4), policy.calculateDelay(2));
    assertEquals(Duration.ofSeconds(8), policy.calculateDelay(3));
    assertEquals(Duration.ofSeconds(16), policy.calculateDelay(4));
  }

  @Test
  void shouldCapDelayAtMaximumBackoff() {
    RetryPolicy policy = new RetryPolicy(10, Duration.ofSeconds(2), 2.0, Duration.ofSeconds(10));

    assertEquals(Duration.ofSeconds(10), policy.calculateDelay(5));
  }

  @Test
  void shouldKeepDelayConstantWhenMultiplierIsOne() {
    RetryPolicy policy = new RetryPolicy(5, Duration.ofSeconds(3), 1.0, Duration.ofSeconds(30));

    assertEquals(Duration.ofSeconds(3), policy.calculateDelay(4));
  }

  @Test
  void shouldHandleVeryLargeAttemptNumbers() {
    RetryPolicy policy = new RetryPolicy(10, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertEquals(Duration.ofSeconds(30), policy.calculateDelay(Integer.MAX_VALUE));
  }

  @Test
  void shouldRejectInvalidCompletedAttemptsForDelayCalculation() {
    RetryPolicy policy = new RetryPolicy(3, Duration.ofSeconds(1), 2.0, Duration.ofSeconds(30));

    assertThrows(IllegalArgumentException.class, () -> policy.calculateDelay(0));
    assertThrows(IllegalArgumentException.class, () -> policy.calculateDelay(-1));
  }
}
