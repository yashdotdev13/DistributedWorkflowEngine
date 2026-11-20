package com.dwe.common.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ClockProviderTest {

  private static final Instant FIXED_INSTANT = Instant.parse("2026-11-20T10:00:00Z");

  @Test
  void shouldReturnTimeFromFixedClock() {
    Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    ClockProvider provider = new ClockProvider(clock);

    assertEquals(FIXED_INSTANT, provider.now());
  }

  @Test
  void shouldCreateSystemUtcProvider() {
    ClockProvider provider = ClockProvider.systemUtc();

    assertEquals(ZoneOffset.UTC, provider.now().atOffset(ZoneOffset.UTC).getOffset());
  }

  @Test
  void shouldRejectNullClock() {
    assertThrows(NullPointerException.class, () -> new ClockProvider(null));
  }
}
