package com.dwe.common.identifier;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

class TaskIdTest extends AbstractUuidIdentifierTest<TaskId> {

  @Override
  protected Supplier<TaskId> generator() {
    return TaskId::generate;
  }

  @Override
  protected Function<String, TaskId> parser() {
    return TaskId::from;
  }

  @Override
  protected Function<UUID, TaskId> constructor() {
    return TaskId::new;
  }

  @Override
  protected UUID valueOf(TaskId identifier) {
    return identifier.value();
  }

  @Override
  protected String stringValue(TaskId identifier) {
    return identifier.toString();
  }
}
