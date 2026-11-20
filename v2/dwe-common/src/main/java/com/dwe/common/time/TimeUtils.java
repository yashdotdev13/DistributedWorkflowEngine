package com.dwe.common.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Provides reusable time and duration operations. */
public final class TimeUtils {

  private TimeUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static boolean isExpired(Instant deadline, Instant currentTime) {
    Objects.requireNonNull(deadline, "Deadline cannot be null");
    Objects.requireNonNull(currentTime, "Current time cannot be null");

    return !currentTime.isBefore(deadline);
  }

  public static boolean isFuture(Instant timestamp, Instant currentTime) {
    Objects.requireNonNull(timestamp, "Timestamp cannot be null");
    Objects.requireNonNull(currentTime, "Current time cannot be null");

    return timestamp.isAfter(currentTime);
  }

  public static Duration elapsed(Instant start, Instant end) {
    Objects.requireNonNull(start, "Start time cannot be null");
    Objects.requireNonNull(end, "End time cannot be null");

    return Duration.between(start, end);
  }

  public static Duration remaining(Instant deadline, Instant currentTime) {
    Objects.requireNonNull(deadline, "Deadline cannot be null");
    Objects.requireNonNull(currentTime, "Current time cannot be null");

    Duration remaining = Duration.between(currentTime, deadline);

    return remaining.isNegative() ? Duration.ZERO : remaining;
  }
}
