package com.dwe.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "task_definitions",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_task_definition_key",
          columnNames = {"workflow_version_id", "task_key"})
    })
public class TaskDefinitionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "workflow_version_id", nullable = false)
  private WorkflowDefinitionVersionEntity workflowVersion;

  @Column(name = "task_key", nullable = false, length = 255)
  private String taskKey;

  @Column(name = "task_type", nullable = false, length = 100)
  private String taskType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "configuration", nullable = false, columnDefinition = "jsonb")
  private String configuration = "{}";

  @Column(name = "max_retries", nullable = false)
  private Integer maxRetries = 0;

  @Column(name = "timeout_seconds", nullable = false)
  private Integer timeoutSeconds;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected TaskDefinitionEntity() {}

  public TaskDefinitionEntity(
      WorkflowDefinitionVersionEntity workflowVersion,
      String taskKey,
      String taskType,
      String configuration,
      Integer maxRetries,
      Integer timeoutSeconds) {
    this.workflowVersion = workflowVersion;
    this.taskKey = taskKey;
    this.taskType = taskType;
    this.configuration = configuration;
    this.maxRetries = maxRetries;
    this.timeoutSeconds = timeoutSeconds;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public WorkflowDefinitionVersionEntity getWorkflowVersion() {
    return workflowVersion;
  }

  public String getTaskKey() {
    return taskKey;
  }

  public String getTaskType() {
    return taskType;
  }

  public String getConfiguration() {
    return configuration;
  }

  public Integer getMaxRetries() {
    return maxRetries;
  }

  public Integer getTimeoutSeconds() {
    return timeoutSeconds;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
