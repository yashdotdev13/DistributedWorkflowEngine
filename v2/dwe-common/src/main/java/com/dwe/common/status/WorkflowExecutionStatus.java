package com.dwe.common.status;

/** Represents the lifecycle states of a workflow execution. */
public enum WorkflowExecutionStatus {
  CREATED,

  RUNNING,

  SUCCEEDED,

  FAILED,

  CANCELLING,

  CANCELLED;

  /**
   * Indicates whether this status represents a terminal state.
   *
   * @return true if the execution has reached a terminal state
   */
  public boolean isTerminal() {
    return this == SUCCEEDED || this == FAILED || this == CANCELLED;
  }
}
