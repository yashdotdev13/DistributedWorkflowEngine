package com.dwe.common.identifier;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

class WorkflowExecutionIdTest extends AbstractUuidIdentifierTest<WorkflowExecutionId> {

  @Override
  protected Supplier<WorkflowExecutionId> generator() {
    return WorkflowExecutionId::generate;
  }

  @Override
  protected Function<String, WorkflowExecutionId> parser() {
    return WorkflowExecutionId::from;
  }

  @Override
  protected Function<UUID, WorkflowExecutionId> constructor() {
    return WorkflowExecutionId::new;
  }

  @Override
  protected UUID valueOf(WorkflowExecutionId identifier) {
    return identifier.value();
  }

  @Override
  protected String stringValue(WorkflowExecutionId identifier) {
    return identifier.toString();
  }
}
