package com.dwe.common.identifier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

abstract class AbstractUuidIdentifierTest<T> {

  protected abstract Supplier<T> generator();

  protected abstract Function<String, T> parser();

  protected abstract Function<UUID, T> constructor();

  protected abstract UUID valueOf(T identifier);

  protected abstract String stringValue(T identifier);

  @Test
  void shouldGenerateIdentifier() {
    T identifier = generator().get();

    assertNotNull(identifier);
    assertNotNull(valueOf(identifier));
  }

  @Test
  void shouldGenerateUniqueIdentifiers() {
    assertNotEquals(generator().get(), generator().get());
  }

  @Test
  void shouldParseUuidString() {
    UUID uuid = UUID.randomUUID();

    T identifier = parser().apply(uuid.toString());

    assertEquals(uuid, valueOf(identifier));
  }

  @Test
  void shouldRejectNullUuid() {
    assertThrows(NullPointerException.class, () -> constructor().apply(null));
  }

  @Test
  void shouldRejectNullString() {
    assertThrows(NullPointerException.class, () -> parser().apply(null));
  }

  @Test
  void shouldRejectInvalidUuid() {
    assertThrows(IllegalArgumentException.class, () -> parser().apply("invalid-uuid"));
  }

  @Test
  void shouldSupportValueBasedEquality() {
    UUID uuid = UUID.randomUUID();

    T first = constructor().apply(uuid);
    T second = constructor().apply(uuid);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldReturnUuidStringRepresentation() {
    UUID uuid = UUID.randomUUID();

    T identifier = constructor().apply(uuid);

    assertEquals(uuid.toString(), stringValue(identifier));
  }
}
