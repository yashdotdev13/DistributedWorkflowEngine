package com.dwe.common.status;

/** Represents the lifecycle states of an individual task attempt. */
public enum TaskAttemptStatus {
  RUNNING,

  SUCCEEDED,

  FAILED,

  TIMED_OUT,

  ABANDONED;

  /**
   * Indicates whether this attempt has reached a terminal state.
   *
   * @return true if the attempt is no longer running
   */
  public boolean isTerminal() {
    return this != RUNNING;
  }
}
