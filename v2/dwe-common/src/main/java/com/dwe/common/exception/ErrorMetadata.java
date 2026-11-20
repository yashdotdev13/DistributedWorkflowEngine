package com.dwe.common.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Represents additional structured information associated with an application error. */
public record ErrorMetadata(Map<String, Object> details) {

  public ErrorMetadata {
    Objects.requireNonNull(details, "Error details cannot be null");
    details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
  }

  public static ErrorMetadata empty() {
    return new ErrorMetadata(Map.of());
  }

  public static ErrorMetadata of(Map<String, Object> details) {
    return new ErrorMetadata(details);
  }
}
