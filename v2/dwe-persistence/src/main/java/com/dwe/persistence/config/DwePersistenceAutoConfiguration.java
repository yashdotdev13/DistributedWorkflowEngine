package com.dwe.persistence.config;

import javax.sql.DataSource;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.flyway.autoconfigure.FlywayConfigurationCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({DataSource.class, FluentConfiguration.class})
@ConditionalOnProperty(
    prefix = "dwe.persistence",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(DwePersistenceProperties.class)
public class DwePersistenceAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(FlywayConfigurationCustomizer.class)
  FlywayConfigurationCustomizer dweFlywayConfigurationCustomizer() {
    return configuration -> {
      configuration.cleanDisabled(true);
      configuration.validateOnMigrate(true);
    };
  }

  @Bean
  @ConditionalOnClass(HibernatePropertiesCustomizer.class)
  @ConditionalOnProperty(
      prefix = "dwe.persistence",
      name = "schema-validation-enabled",
      havingValue = "true",
      matchIfMissing = true)
  @ConditionalOnMissingBean(HibernatePropertiesCustomizer.class)
  HibernatePropertiesCustomizer dweHibernatePropertiesCustomizer() {
    return properties -> properties.putIfAbsent("hibernate.hbm2ddl.auto", "validate");
  }
}
