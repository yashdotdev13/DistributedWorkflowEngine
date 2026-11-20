package com.dwe.common.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageRequestTest {

  @Test
  void shouldCreatePageRequestWithDefaultAscendingDirection() {
    PageRequest request = PageRequest.of(0, 20);

    assertEquals(0, request.page());
    assertEquals(20, request.size());
    assertEquals(SortDirection.ASCENDING, request.sortDirection());
  }

  @Test
  void shouldCreatePageRequestWithExplicitDirection() {
    PageRequest request = new PageRequest(2, 10, SortDirection.DESCENDING);

    assertEquals(SortDirection.DESCENDING, request.sortDirection());
  }

  @Test
  void shouldCalculateOffset() {
    assertEquals(20L, PageRequest.of(2, 10).offset());
  }

  @Test
  void shouldCalculateLargeOffsetWithoutIntegerOverflow() {
    PageRequest request = PageRequest.of(Integer.MAX_VALUE, Integer.MAX_VALUE);

    assertEquals((long) Integer.MAX_VALUE * Integer.MAX_VALUE, request.offset());
  }

  @Test
  void shouldRejectNegativePageNumber() {
    assertThrows(IllegalArgumentException.class, () -> PageRequest.of(-1, 10));
  }

  @Test
  void shouldRejectZeroPageSize() {
    assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, 0));
  }

  @Test
  void shouldRejectNegativePageSize() {
    assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, -10));
  }

  @Test
  void shouldRejectNullSortDirection() {
    assertThrows(NullPointerException.class, () -> new PageRequest(0, 10, null));
  }

  @Test
  void shouldReturnNextPage() {
    PageRequest request = PageRequest.of(1, 20);
    PageRequest next = request.next();

    assertEquals(2, next.page());
    assertEquals(20, next.size());
    assertEquals(request.sortDirection(), next.sortDirection());
  }

  @Test
  void shouldReturnPreviousPage() {
    PageRequest request = new PageRequest(2, 20, SortDirection.DESCENDING);
    PageRequest previous = request.previous();

    assertEquals(1, previous.page());
    assertEquals(20, previous.size());
    assertEquals(SortDirection.DESCENDING, previous.sortDirection());
  }

  @Test
  void shouldRejectPreviousPageFromFirstPage() {
    assertThrows(IllegalStateException.class, () -> PageRequest.of(0, 20).previous());
  }

  @Test
  void shouldRejectNextPageWhenPageNumberOverflows() {
    PageRequest request = PageRequest.of(Integer.MAX_VALUE, 10);

    assertThrows(ArithmeticException.class, request::next);
  }
}
