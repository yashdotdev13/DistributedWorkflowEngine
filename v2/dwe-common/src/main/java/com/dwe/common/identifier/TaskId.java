package com.dwe.common.identifier;

import java.util.Objects;
import java.util.UUID;

/** Strongly typed identifier for a task definition. */
public record TaskId(UUID value) {

  public TaskId {
    Objects.requireNonNull(value, "Task ID cannot be null");
  }

  public static TaskId generate() {
    return new TaskId(UUID.randomUUID());
  }

  public static TaskId from(String value) {
    Objects.requireNonNull(value, "Task ID cannot be null");
    return new TaskId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
