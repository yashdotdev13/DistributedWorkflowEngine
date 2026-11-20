package com.dwe.common.identifier;

import java.util.Objects;
import java.util.UUID;

/** Identifies a particular execution of a task definition. */
public record TaskExecutionId(UUID value) {

  public TaskExecutionId {
    Objects.requireNonNull(value, "Task execution ID cannot be null");
  }

  public static TaskExecutionId generate() {
    return new TaskExecutionId(UUID.randomUUID());
  }

  public static TaskExecutionId from(String value) {
    Objects.requireNonNull(value, "Task execution ID cannot be null");
    return new TaskExecutionId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
