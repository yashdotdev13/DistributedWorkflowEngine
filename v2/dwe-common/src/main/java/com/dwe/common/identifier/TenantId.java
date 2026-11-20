package com.dwe.common.identifier;

import java.util.Objects;
import java.util.UUID;

/** Identifies a tenant within the workflow engine. */
public record TenantId(UUID value) {

  public TenantId {
    Objects.requireNonNull(value, "Tenant ID cannot be null");
  }

  public static TenantId generate() {
    return new TenantId(UUID.randomUUID());
  }

  public static TenantId from(String value) {
    Objects.requireNonNull(value, "Tenant ID cannot be null");
    return new TenantId(UUID.fromString(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
