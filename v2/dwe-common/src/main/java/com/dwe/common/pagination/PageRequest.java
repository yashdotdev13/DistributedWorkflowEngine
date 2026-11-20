package com.dwe.common.pagination;

/**
 * Represents a request for a specific page of data.
 *
 * @param page the zero-based page number
 * @param size the maximum number of records per page
 * @param sortDirection the requested sorting direction
 */
public record PageRequest(int page, int size, SortDirection sortDirection) {

  public PageRequest {
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }

    if (size <= 0) {
      throw new IllegalArgumentException("Page size must be greater than zero");
    }

    if (sortDirection == null) {
      throw new NullPointerException("Sort direction cannot be null");
    }

    Math.multiplyExact((long) page, size);
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size, SortDirection.ASCENDING);
  }

  public long offset() {
    return (long) page * size;
  }

  public PageRequest next() {
    return new PageRequest(Math.addExact(page, 1), size, sortDirection);
  }

  public PageRequest previous() {
    if (page == 0) {
      throw new IllegalStateException("First page has no previous page");
    }

    return new PageRequest(page - 1, size, sortDirection);
  }
}
