package com.dwe.common.time;

import java.time.Instant;

/** Provides the current time through an injectable abstraction. */
@FunctionalInterface
public interface TimeProvider {

  Instant now();

  static TimeProvider system() {
    return Instant::now;
  }
}
