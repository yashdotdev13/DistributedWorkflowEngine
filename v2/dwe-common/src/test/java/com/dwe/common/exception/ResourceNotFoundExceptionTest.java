package com.dwe.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

  @Test
  void shouldUseResourceNotFoundErrorCode() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Workflow not found");

    assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    assertEquals("Workflow not found", exception.getMessage());
  }

  @Test
  void shouldAcceptErrorMetadata() {
    ErrorMetadata metadata =
        ErrorMetadata.of(Map.of("resourceType", "Workflow", "resourceId", "workflow-123"));

    ResourceNotFoundException exception =
        new ResourceNotFoundException("Workflow not found", metadata);

    assertSame(metadata, exception.getMetadata());
    assertEquals("Workflow", exception.getMetadata().details().get("resourceType"));
    assertEquals("workflow-123", exception.getMetadata().details().get("resourceId"));
  }

  @Test
  void shouldAcceptExceptionCause() {
    RuntimeException cause = new RuntimeException("Database lookup failed");

    ResourceNotFoundException exception =
        new ResourceNotFoundException("Workflow not found", cause);

    assertSame(cause, exception.getCause());
  }

  @Test
  void shouldProvideEmptyMetadataByDefault() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Workflow not found");

    assertTrue(exception.getMetadata().details().isEmpty());
  }
}
