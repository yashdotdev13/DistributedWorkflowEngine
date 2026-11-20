package com.dwe.common.status;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskExecutionStatusTest {

  @Test
  void shouldIdentifyTerminalStatuses() {
    assertTrue(TaskExecutionStatus.SUCCEEDED.isTerminal());
    assertTrue(TaskExecutionStatus.FAILED.isTerminal());
    assertTrue(TaskExecutionStatus.SKIPPED.isTerminal());
    assertTrue(TaskExecutionStatus.CANCELLED.isTerminal());
  }

  @Test
  void shouldIdentifyNonTerminalStatuses() {
    assertFalse(TaskExecutionStatus.PENDING.isTerminal());
    assertFalse(TaskExecutionStatus.READY.isTerminal());
    assertFalse(TaskExecutionStatus.RUNNING.isTerminal());
    assertFalse(TaskExecutionStatus.RETRY_WAIT.isTerminal());
  }
}
