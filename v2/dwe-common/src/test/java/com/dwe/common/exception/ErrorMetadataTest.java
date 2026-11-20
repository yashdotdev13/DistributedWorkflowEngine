package com.dwe.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorMetadataTest {

  @Test
  void shouldCreateEmptyMetadata() {
    ErrorMetadata metadata = ErrorMetadata.empty();

    assertTrue(metadata.details().isEmpty());
  }

  @Test
  void shouldPreserveProvidedDetails() {
    Map<String, Object> details =
        Map.of(
            "resourceType", "Workflow",
            "resourceId", "workflow-123");

    ErrorMetadata metadata = ErrorMetadata.of(details);

    assertEquals(details, metadata.details());
  }

  @Test
  void shouldPreserveInsertionOrder() {
    Map<String, Object> details = new LinkedHashMap<>();
    details.put("first", "value-1");
    details.put("second", "value-2");

    ErrorMetadata metadata = ErrorMetadata.of(details);

    assertEquals(
        java.util.List.of("first", "second"), java.util.List.copyOf(metadata.details().keySet()));
  }

  @Test
  void shouldDefensivelyCopyProvidedMap() {
    Map<String, Object> details = new LinkedHashMap<>();
    details.put("resourceId", "workflow-123");

    ErrorMetadata metadata = ErrorMetadata.of(details);
    details.put("resourceId", "workflow-456");

    assertEquals("workflow-123", metadata.details().get("resourceId"));
  }

  @Test
  void shouldPreventModificationOfDetails() {
    ErrorMetadata metadata = ErrorMetadata.of(Map.of("resourceId", "workflow-123"));

    assertThrows(
        UnsupportedOperationException.class,
        () -> metadata.details().put("resourceType", "Workflow"));
  }

  @Test
  void shouldRejectNullDetails() {
    assertThrows(NullPointerException.class, () -> ErrorMetadata.of(null));
  }
}
