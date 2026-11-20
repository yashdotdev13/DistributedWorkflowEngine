package com.dwe.common.retry;

/** Determines whether a failure is eligible for retry. */
@FunctionalInterface
public interface RetryableErrorClassifier {

  /**
   * Determines whether the supplied failure is retryable.
   *
   * @param failure the failure to classify
   * @return true if the failure is retryable
   */
  boolean isRetryable(Throwable failure);

  /**
   * Creates a classifier that treats every failure as retryable.
   *
   * @return an always-retryable classifier
   */
  static RetryableErrorClassifier alwaysRetryable() {
    return failure -> true;
  }

  /**
   * Creates a classifier that treats every failure as non-retryable.
   *
   * @return a never-retryable classifier
   */
  static RetryableErrorClassifier neverRetryable() {
    return failure -> false;
  }
}
