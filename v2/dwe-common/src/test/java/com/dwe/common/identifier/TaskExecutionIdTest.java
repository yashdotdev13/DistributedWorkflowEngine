package com.dwe.common.identifier;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

class TaskExecutionIdTest extends AbstractUuidIdentifierTest<TaskExecutionId> {

  @Override
  protected Supplier<TaskExecutionId> generator() {
    return TaskExecutionId::generate;
  }

  @Override
  protected Function<String, TaskExecutionId> parser() {
    return TaskExecutionId::from;
  }

  @Override
  protected Function<UUID, TaskExecutionId> constructor() {
    return TaskExecutionId::new;
  }

  @Override
  protected UUID valueOf(TaskExecutionId identifier) {
    return identifier.value();
  }

  @Override
  protected String stringValue(TaskExecutionId identifier) {
    return identifier.toString();
  }
}
