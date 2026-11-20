package com.dwe.persistence.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.flyway.autoconfigure.FlywayConfigurationCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DwePersistenceAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(DwePersistenceAutoConfiguration.class));

  @Test
  void shouldRegisterDefaultPersistenceConfiguration() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(DwePersistenceProperties.class);
          assertThat(context).hasSingleBean(FlywayConfigurationCustomizer.class);
          assertThat(context).hasSingleBean(HibernatePropertiesCustomizer.class);
        });
  }

  @Test
  void shouldDisablePersistenceWhenConfigured() {
    contextRunner
        .withPropertyValues("dwe.persistence.enabled=false")
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(DwePersistenceProperties.class);
              assertThat(context).doesNotHaveBean(FlywayConfigurationCustomizer.class);
              assertThat(context).doesNotHaveBean(HibernatePropertiesCustomizer.class);
            });
  }

  @Test
  void shouldDisableHibernateSchemaValidationWhenConfigured() {
    contextRunner
        .withPropertyValues("dwe.persistence.schema-validation-enabled=false")
        .run(
            context -> {
              assertThat(context).hasSingleBean(DwePersistenceProperties.class);
              assertThat(context).hasSingleBean(FlywayConfigurationCustomizer.class);
              assertThat(context).doesNotHaveBean(HibernatePropertiesCustomizer.class);
            });
  }
}
