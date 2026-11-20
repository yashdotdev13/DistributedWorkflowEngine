package com.dwe.common.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TimeProviderTest {

  @Test
  void shouldProvideTimeThroughFunctionalImplementation() {
    Instant expected = Instant.parse("2026-11-20T10:00:00Z");

    TimeProvider provider = () -> expected;

    assertEquals(expected, provider.now());
  }

  @Test
  void shouldCreateSystemTimeProvider() {
    TimeProvider provider = TimeProvider.system();

    assertNotNull(provider.now());
  }
}
