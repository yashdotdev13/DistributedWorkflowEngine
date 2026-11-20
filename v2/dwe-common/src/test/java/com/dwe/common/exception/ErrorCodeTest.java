package com.dwe.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ErrorCodeTest {

  @Test
  void shouldDefineExpectedErrorCodes() {
    assertEquals(7, ErrorCode.values().length);

    assertNotNull(ErrorCode.valueOf("VALIDATION_ERROR"));
    assertNotNull(ErrorCode.valueOf("RESOURCE_NOT_FOUND"));
    assertNotNull(ErrorCode.valueOf("RESOURCE_ALREADY_EXISTS"));
    assertNotNull(ErrorCode.valueOf("BUSINESS_RULE_VIOLATION"));
    assertNotNull(ErrorCode.valueOf("OPERATION_NOT_ALLOWED"));
    assertNotNull(ErrorCode.valueOf("CONCURRENCY_CONFLICT"));
    assertNotNull(ErrorCode.valueOf("INTERNAL_ERROR"));
  }
}
