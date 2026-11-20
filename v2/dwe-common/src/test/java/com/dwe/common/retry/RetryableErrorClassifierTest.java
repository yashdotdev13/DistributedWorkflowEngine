package com.dwe.common.retry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RetryableErrorClassifierTest {

  @Test
  void shouldClassifyAllFailuresAsRetryable() {
    RetryableErrorClassifier classifier = RetryableErrorClassifier.alwaysRetryable();

    assertTrue(classifier.isRetryable(new RuntimeException("Temporary failure")));
    assertTrue(classifier.isRetryable(new IllegalArgumentException("Invalid input")));
  }

  @Test
  void shouldClassifyAllFailuresAsNonRetryable() {
    RetryableErrorClassifier classifier = RetryableErrorClassifier.neverRetryable();

    assertFalse(classifier.isRetryable(new RuntimeException("Temporary failure")));
    assertFalse(classifier.isRetryable(new IllegalArgumentException("Invalid input")));
  }

  @Test
  void shouldSupportCustomRetryClassification() {
    RetryableErrorClassifier classifier = failure -> failure instanceof IllegalStateException;

    assertTrue(classifier.isRetryable(new IllegalStateException("Temporary state")));
    assertFalse(classifier.isRetryable(new IllegalArgumentException("Invalid input")));
  }

  @Test
  void shouldSupportClassificationBasedOnExceptionMessage() {
    RetryableErrorClassifier classifier =
        failure -> failure.getMessage() != null && failure.getMessage().contains("timeout");

    assertTrue(classifier.isRetryable(new RuntimeException("Connection timeout")));
    assertFalse(classifier.isRetryable(new RuntimeException("Invalid request")));
  }
}
