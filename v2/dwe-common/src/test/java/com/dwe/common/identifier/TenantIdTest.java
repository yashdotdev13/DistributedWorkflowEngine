package com.dwe.common.identifier;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

class TenantIdTest extends AbstractUuidIdentifierTest<TenantId> {

  @Override
  protected Supplier<TenantId> generator() {
    return TenantId::generate;
  }

  @Override
  protected Function<String, TenantId> parser() {
    return TenantId::from;
  }

  @Override
  protected Function<UUID, TenantId> constructor() {
    return TenantId::new;
  }

  @Override
  protected UUID valueOf(TenantId identifier) {
    return identifier.value();
  }

  @Override
  protected String stringValue(TenantId identifier) {
    return identifier.toString();
  }
}
