package com.dwe.common.time;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Provides time using a configurable Java Clock.
 *
 * <p>This allows deterministic time control in tests.
 */
public final class ClockProvider implements TimeProvider {

  private final Clock clock;

  public ClockProvider(Clock clock) {
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  public static ClockProvider systemUtc() {
    return new ClockProvider(Clock.systemUTC());
  }

  @Override
  public Instant now() {
    return clock.instant();
  }
}
