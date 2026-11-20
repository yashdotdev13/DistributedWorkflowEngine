package com.dwe.common.status;

/** Represents the lifecycle states of a task execution. */
public enum TaskExecutionStatus {
  PENDING,

  READY,

  RUNNING,

  RETRY_WAIT,

  SUCCEEDED,

  FAILED,

  SKIPPED,

  CANCELLED;

  /**
   * Indicates whether this status represents a terminal state.
   *
   * @return true if the task execution has reached a terminal state
   */
  public boolean isTerminal() {
    return this == SUCCEEDED || this == FAILED || this == SKIPPED || this == CANCELLED;
  }
}
