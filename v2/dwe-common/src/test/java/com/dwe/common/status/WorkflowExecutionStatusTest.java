package com.dwe.common.status;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WorkflowExecutionStatusTest {

  @Test
  void shouldIdentifyTerminalStatuses() {
    assertTrue(WorkflowExecutionStatus.SUCCEEDED.isTerminal());
    assertTrue(WorkflowExecutionStatus.FAILED.isTerminal());
    assertTrue(WorkflowExecutionStatus.CANCELLED.isTerminal());
  }

  @Test
  void shouldIdentifyNonTerminalStatuses() {
    assertFalse(WorkflowExecutionStatus.CREATED.isTerminal());
    assertFalse(WorkflowExecutionStatus.RUNNING.isTerminal());
    assertFalse(WorkflowExecutionStatus.CANCELLING.isTerminal());
  }
}
