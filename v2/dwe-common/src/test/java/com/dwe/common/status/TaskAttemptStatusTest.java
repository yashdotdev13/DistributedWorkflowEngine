package com.dwe.common.status;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskAttemptStatusTest {

  @Test
  void shouldIdentifyTerminalStatuses() {
    assertTrue(TaskAttemptStatus.SUCCEEDED.isTerminal());
    assertTrue(TaskAttemptStatus.FAILED.isTerminal());
    assertTrue(TaskAttemptStatus.TIMED_OUT.isTerminal());
    assertTrue(TaskAttemptStatus.ABANDONED.isTerminal());
  }

  @Test
  void shouldIdentifyNonTerminalStatuses() {
    assertFalse(TaskAttemptStatus.RUNNING.isTerminal());
  }
}
