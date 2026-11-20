package com.dwe.persistence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dwe.persistence")
public class DwePersistenceProperties {

  private boolean enabled = true;

  private boolean schemaValidationEnabled = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isSchemaValidationEnabled() {
    return schemaValidationEnabled;
  }

  public void setSchemaValidationEnabled(boolean schemaValidationEnabled) {
    this.schemaValidationEnabled = schemaValidationEnabled;
  }
}
