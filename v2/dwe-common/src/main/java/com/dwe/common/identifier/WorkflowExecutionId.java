package com.dwe.common.identifier;

import java.util.Objects;
import java.util.UUID;

/** Identifies a particular execution of a workflow definition. */
public record WorkflowExecutionId(UUID value) {

  public WorkflowExecutionId {
    Objects.requireNonNull(value, "Workflow execution ID cannot be null");
  }

  public static WorkflowExecutionId generate() {
    return new WorkflowExecutionId(UUID.randomUUID());
  }

  public static WorkflowExecutionId from(String value) {
    Objects.requireNonNull(value, "Workflow execution ID cannot be null");
    return new WorkflowExecutionId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
