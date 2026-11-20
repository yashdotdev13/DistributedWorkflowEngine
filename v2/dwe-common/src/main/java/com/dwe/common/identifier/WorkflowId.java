package com.dwe.common.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a workflow definition.
 *
 * <p>WorkflowId is immutable and backed by a UUID.
 */
public record WorkflowId(UUID value) {

  public WorkflowId {
    Objects.requireNonNull(value, "Workflow ID cannot be null");
  }

  public static WorkflowId generate() {
    return new WorkflowId(UUID.randomUUID());
  }

  public static WorkflowId from(String value) {
    Objects.requireNonNull(value, "Workflow ID cannot be null");
    return new WorkflowId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
