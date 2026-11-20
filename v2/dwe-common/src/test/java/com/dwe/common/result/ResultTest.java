package com.dwe.common.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dwe.common.exception.ErrorCode;
import com.dwe.common.exception.ErrorMetadata;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  void shouldCreateSuccessfulResult() {
    Result<String> result = Result.success("workflow-created");

    assertTrue(result.isSuccess());
    assertFalse(result.isFailure());
    assertEquals("workflow-created", result.value().orElseThrow());
    assertTrue(result.error().isEmpty());
  }

  @Test
  void shouldCreateFailedResult() {
    Result<String> result = Result.failure(ErrorCode.VALIDATION_ERROR, "Invalid workflow");

    assertTrue(result.isFailure());
    assertFalse(result.isSuccess());
    assertTrue(result.value().isEmpty());
    assertEquals(ErrorCode.VALIDATION_ERROR, result.error().orElseThrow().code());
    assertEquals("Invalid workflow", result.error().orElseThrow().message());
  }

  @Test
  void shouldPreserveFailureMetadata() {
    ErrorMetadata metadata = ErrorMetadata.of(Map.of("field", "workflowName"));

    Result<String> result = Result.failure(ErrorCode.VALIDATION_ERROR, "Invalid name", metadata);

    assertEquals("workflowName", result.error().orElseThrow().metadata().details().get("field"));
  }

  @Test
  void shouldMapSuccessfulResult() {
    Result<Integer> result = Result.success(10).map(value -> value * 2);

    assertEquals(20, result.value().orElseThrow());
    assertTrue(result.isSuccess());
  }

  @Test
  void shouldPreserveFailureDuringMapping() {
    Result<Integer> original = Result.failure(ErrorCode.INTERNAL_ERROR, "Operation failed");

    Result<String> mapped = original.map(Object::toString);

    assertTrue(mapped.isFailure());
    assertEquals(original.error().orElseThrow(), mapped.error().orElseThrow());
  }

  @Test
  void shouldRejectNullMapper() {
    Result<String> result = Result.success("value");

    assertThrows(NullPointerException.class, () -> result.map(null));
  }

  @Test
  void shouldFlatMapSuccessfulResult() {
    Result<Integer> result = Result.success(10).flatMap(value -> Result.success(value * 2));

    assertEquals(20, result.value().orElseThrow());
  }

  @Test
  void shouldFlatMapIntoFailure() {
    Result<Integer> result =
        Result.success(10)
            .flatMap(
                value ->
                    Result.failure(ErrorCode.BUSINESS_RULE_VIOLATION, "Operation not allowed"));

    assertTrue(result.isFailure());
    assertEquals(ErrorCode.BUSINESS_RULE_VIOLATION, result.error().orElseThrow().code());
  }

  @Test
  void shouldPreserveFailureDuringFlatMap() {
    Result<Integer> original = Result.failure(ErrorCode.INTERNAL_ERROR, "Original failure");

    Result<String> result = original.flatMap(value -> Result.success(value.toString()));

    assertEquals(original.error().orElseThrow(), result.error().orElseThrow());
  }

  @Test
  void shouldRejectNullFlatMapResult() {
    Result<String> result = Result.success("value");

    assertThrows(NullPointerException.class, () -> result.flatMap(value -> null));
  }

  @Test
  void shouldReturnSuccessfulValueUsingOrElse() {
    Result<String> result = Result.success("workflow");

    assertEquals("workflow", result.orElse("fallback"));
  }

  @Test
  void shouldReturnFallbackForFailedResult() {
    Result<String> result = Result.failure(ErrorCode.INTERNAL_ERROR, "Failure");

    assertEquals("fallback", result.orElse("fallback"));
  }

  @Test
  void shouldRejectNullSuccessValue() {
    assertThrows(NullPointerException.class, () -> Result.success(null));
  }

  @Test
  void shouldRejectNullFailureCode() {
    assertThrows(NullPointerException.class, () -> Result.failure(null, "Failure"));
  }

  @Test
  void shouldRejectNullFailureMessage() {
    assertThrows(NullPointerException.class, () -> Result.failure(ErrorCode.INTERNAL_ERROR, null));
  }

  @Test
  void shouldRejectNullFailureMetadata() {
    assertThrows(
        NullPointerException.class,
        () -> Result.failure(ErrorCode.INTERNAL_ERROR, "Failure", null));
  }

  @Test
  void shouldRejectBlankFailureMessage() {
    assertThrows(
        IllegalArgumentException.class, () -> Result.failure(ErrorCode.INTERNAL_ERROR, " "));
  }

  @Test
  void shouldRejectNullFailureDetails() {
    assertThrows(NullPointerException.class, () -> new Result.Failure<String>(null));
  }

  @Test
  void shouldRejectNullFailureDetailsProperties() {
    assertThrows(
        NullPointerException.class,
        () -> new Result.FailureDetails(null, "Failure", ErrorMetadata.empty()));

    assertThrows(
        NullPointerException.class,
        () -> new Result.FailureDetails(ErrorCode.INTERNAL_ERROR, null, ErrorMetadata.empty()));

    assertThrows(
        NullPointerException.class,
        () -> new Result.FailureDetails(ErrorCode.INTERNAL_ERROR, "Failure", null));
  }
}
