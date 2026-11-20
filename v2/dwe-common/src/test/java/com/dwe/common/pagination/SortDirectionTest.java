package com.dwe.common.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SortDirectionTest {

  @Test
  void shouldDefineAscendingDirection() {
    assertEquals(SortDirection.ASCENDING, SortDirection.valueOf("ASCENDING"));
  }

  @Test
  void shouldDefineDescendingDirection() {
    assertEquals(SortDirection.DESCENDING, SortDirection.valueOf("DESCENDING"));
  }

  @Test
  void shouldContainExactlyTwoDirections() {
    assertEquals(2, SortDirection.values().length);
  }
}
