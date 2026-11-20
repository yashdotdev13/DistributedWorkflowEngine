package com.dwe.common.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TimeUtilsTest {

  private static final Instant NOW = Instant.parse("2026-11-20T10:00:00Z");

  @Test
  void shouldIdentifyExpiredDeadline() {
    assertTrue(TimeUtils.isExpired(NOW.minusSeconds(1), NOW));
  }

  @Test
  void shouldTreatDeadlineEqualToCurrentTimeAsExpired() {
    assertTrue(TimeUtils.isExpired(NOW, NOW));
  }

  @Test
  void shouldIdentifyNonExpiredDeadline() {
    assertFalse(TimeUtils.isExpired(NOW.plusSeconds(1), NOW));
  }

  @Test
  void shouldIdentifyFutureTimestamp() {
    assertTrue(TimeUtils.isFuture(NOW.plusSeconds(1), NOW));
  }

  @Test
  void shouldIdentifyCurrentTimestampAsNotFuture() {
    assertFalse(TimeUtils.isFuture(NOW, NOW));
  }

  @Test
  void shouldIdentifyPastTimestampAsNotFuture() {
    assertFalse(TimeUtils.isFuture(NOW.minusSeconds(1), NOW));
  }

  @Test
  void shouldCalculateElapsedDuration() {
    Duration elapsed = TimeUtils.elapsed(NOW, NOW.plusSeconds(30));

    assertEquals(Duration.ofSeconds(30), elapsed);
  }

  @Test
  void shouldAllowNegativeElapsedDuration() {
    Duration elapsed = TimeUtils.elapsed(NOW.plusSeconds(30), NOW);

    assertEquals(Duration.ofSeconds(-30), elapsed);
  }

  @Test
  void shouldCalculateRemainingDuration() {
    Duration remaining = TimeUtils.remaining(NOW.plusSeconds(60), NOW);

    assertEquals(Duration.ofSeconds(60), remaining);
  }

  @Test
  void shouldReturnZeroWhenDeadlineHasPassed() {
    Duration remaining = TimeUtils.remaining(NOW.minusSeconds(10), NOW);

    assertEquals(Duration.ZERO, remaining);
  }

  @Test
  void shouldReturnZeroWhenDeadlineEqualsCurrentTime() {
    Duration remaining = TimeUtils.remaining(NOW, NOW);

    assertEquals(Duration.ZERO, remaining);
  }

  @Test
  void shouldRejectNullDeadline() {
    assertThrows(NullPointerException.class, () -> TimeUtils.isExpired(null, NOW));
  }

  @Test
  void shouldRejectNullCurrentTime() {
    assertThrows(NullPointerException.class, () -> TimeUtils.isExpired(NOW, null));
  }

  @Test
  void shouldRejectNullTimestamp() {
    assertThrows(NullPointerException.class, () -> TimeUtils.isFuture(null, NOW));
  }

  @Test
  void shouldRejectNullStartTime() {
    assertThrows(NullPointerException.class, () -> TimeUtils.elapsed(null, NOW));
  }

  @Test
  void shouldRejectNullEndTime() {
    assertThrows(NullPointerException.class, () -> TimeUtils.elapsed(NOW, null));
  }

  @Test
  void shouldRejectNullDeadlineWhenCalculatingRemainingTime() {
    assertThrows(NullPointerException.class, () -> TimeUtils.remaining(null, NOW));
  }
}
