package com.dwe.common.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void shouldCreatePage() {
    Page<String> page = new Page<>(List.of("workflow-1", "workflow-2"), PageRequest.of(0, 10), 25);

    assertEquals(2, page.numberOfElements());
    assertEquals(25, page.totalElements());
    assertEquals(3, page.totalPages());
    assertFalse(page.isEmpty());
  }

  @Test
  void shouldCreateEmptyPage() {
    Page<String> page = new Page<>(List.of(), PageRequest.of(0, 10), 0);

    assertTrue(page.isEmpty());
    assertEquals(0, page.numberOfElements());
    assertEquals(0, page.totalPages());
  }

  @Test
  void shouldCalculateTotalPages() {
    Page<String> page = new Page<>(List.of("workflow-1"), PageRequest.of(0, 10), 21);

    assertEquals(3, page.totalPages());
  }

  @Test
  void shouldCalculateTotalPagesWhenEvenlyDivisible() {
    Page<String> page = new Page<>(List.of("workflow-1"), PageRequest.of(0, 10), 20);

    assertEquals(2, page.totalPages());
  }

  @Test
  void shouldCalculateTotalPagesWithoutOverflow() {
    Page<String> page = new Page<>(List.of(), PageRequest.of(0, 1), Long.MAX_VALUE);

    assertEquals(Long.MAX_VALUE, page.totalPages());
  }

  @Test
  void shouldIdentifyFirstPage() {
    Page<String> page = new Page<>(List.of("workflow-1"), PageRequest.of(0, 10), 20);

    assertTrue(page.isFirst());
    assertTrue(page.hasNext());
    assertFalse(page.hasPrevious());
    assertFalse(page.isLast());
  }

  @Test
  void shouldIdentifyLastPage() {
    Page<String> page = new Page<>(List.of("workflow-2"), PageRequest.of(1, 10), 11);

    assertFalse(page.isFirst());
    assertFalse(page.hasNext());
    assertTrue(page.hasPrevious());
    assertTrue(page.isLast());
  }

  @Test
  void shouldIdentifySinglePageResult() {
    Page<String> page = new Page<>(List.of("workflow-1"), PageRequest.of(0, 10), 1);

    assertTrue(page.isFirst());
    assertTrue(page.isLast());
    assertFalse(page.hasNext());
    assertFalse(page.hasPrevious());
  }

  @Test
  void shouldDefensivelyCopyContent() {
    List<String> content = new ArrayList<>(List.of("workflow-1"));

    Page<String> page = new Page<>(content, PageRequest.of(0, 10), 1);
    content.clear();

    assertEquals(1, page.content().size());
  }

  @Test
  void shouldPreventContentModification() {
    Page<String> page = new Page<>(List.of("workflow-1"), PageRequest.of(0, 10), 1);

    assertThrows(UnsupportedOperationException.class, () -> page.content().add("workflow-2"));
  }

  @Test
  void shouldRejectNullContent() {
    assertThrows(NullPointerException.class, () -> new Page<>(null, PageRequest.of(0, 10), 0));
  }

  @Test
  void shouldRejectNullPageRequest() {
    assertThrows(NullPointerException.class, () -> new Page<>(List.of(), null, 0));
  }

  @Test
  void shouldRejectNegativeTotalElements() {
    assertThrows(
        IllegalArgumentException.class, () -> new Page<>(List.of(), PageRequest.of(0, 10), -1));
  }

  @Test
  void shouldRejectContentExceedingPageSize() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Page<>(List.of("one", "two", "three"), PageRequest.of(0, 2), 3));
  }

  @Test
  void shouldRejectContentExceedingTotalElements() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Page<>(List.of("one", "two"), PageRequest.of(0, 10), 1));
  }
}
