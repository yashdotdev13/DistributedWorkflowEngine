package com.dwe.common.pagination;

import java.util.List;
import java.util.Objects;

/**
 * Represents a page of results along with pagination metadata.
 *
 * @param <T> the type of elements contained in the page
 * @param content the elements in the current page
 * @param pageRequest the request that produced this page
 * @param totalElements the total number of matching elements
 */
public record Page<T>(List<T> content, PageRequest pageRequest, long totalElements) {

  public Page {
    Objects.requireNonNull(content, "Page content cannot be null");
    Objects.requireNonNull(pageRequest, "Page request cannot be null");

    if (totalElements < 0) {
      throw new IllegalArgumentException("Total elements cannot be negative");
    }

    if (content.size() > pageRequest.size()) {
      throw new IllegalArgumentException("Page content cannot exceed the requested page size");
    }

    if (content.size() > totalElements) {
      throw new IllegalArgumentException("Page content cannot exceed total elements");
    }

    content = List.copyOf(content);
  }

  public int numberOfElements() {
    return content.size();
  }

  public boolean isEmpty() {
    return content.isEmpty();
  }

  public boolean isFirst() {
    return pageRequest.page() == 0;
  }

  public boolean isLast() {
    return !hasNext();
  }

  public boolean hasPrevious() {
    return pageRequest.page() > 0;
  }

  public boolean hasNext() {
    return (long) pageRequest.page() + 1 < totalPages();
  }

  public long totalPages() {
    long size = pageRequest.size();

    return totalElements / size + (totalElements % size == 0 ? 0 : 1);
  }
}
