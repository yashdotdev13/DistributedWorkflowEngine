package com.dwe.common.identifier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class WorkflowIdTest {

  @Test
  void shouldGenerateUniqueWorkflowIds() {
    WorkflowId first = WorkflowId.generate();
    WorkflowId second = WorkflowId.generate();

    assertNotNull(first);
    assertNotNull(second);
    assertNotEquals(first, second);
  }

  @Test
  void shouldCreateWorkflowIdFromUuid() {
    UUID uuid = UUID.randomUUID();

    WorkflowId workflowId = new WorkflowId(uuid);

    assertEquals(uuid, workflowId.value());
  }

  @Test
  void shouldParseWorkflowIdFromString() {
    UUID uuid = UUID.randomUUID();

    WorkflowId workflowId = WorkflowId.from(uuid.toString());

    assertEquals(uuid, workflowId.value());
  }

  @Test
  void shouldRejectNullUuid() {
    assertThrows(NullPointerException.class, () -> new WorkflowId(null));
  }

  @Test
  void shouldRejectNullString() {
    assertThrows(NullPointerException.class, () -> WorkflowId.from(null));
  }

  @Test
  void shouldRejectInvalidUuidString() {
    assertThrows(IllegalArgumentException.class, () -> WorkflowId.from("invalid-uuid"));
  }

  @Test
  void shouldHaveValueBasedEquality() {
    UUID uuid = UUID.randomUUID();

    WorkflowId first = new WorkflowId(uuid);
    WorkflowId second = new WorkflowId(uuid);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldReturnUuidAsString() {
    UUID uuid = UUID.randomUUID();
    WorkflowId workflowId = new WorkflowId(uuid);

    assertEquals(uuid.toString(), workflowId.toString());
  }
}
