package com.dwe.persistence.entity;

import com.dwe.common.status.WorkflowVersionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(
    name = "workflow_definition_versions",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_workflow_definition_version",
          columnNames = {"workflow_definition_id", "version"})
    })
public class WorkflowDefinitionVersionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, updatable = false)
  private Integer version;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "workflow_definition_id", nullable = false, updatable = false)
  private WorkflowDefinitionEntity workflowDefinition;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private WorkflowVersionStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  protected WorkflowDefinitionVersionEntity() {}

  public WorkflowDefinitionVersionEntity(
      WorkflowDefinitionEntity workflowDefinition, Integer version) {
    this.workflowDefinition = workflowDefinition;
    this.version = version;
    this.status = WorkflowVersionStatus.DRAFT;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public WorkflowDefinitionEntity getWorkflowDefinition() {
    return workflowDefinition;
  }

  public Integer getVersion() {
    return version;
  }

  public WorkflowVersionStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void publish() {
    if (status != WorkflowVersionStatus.DRAFT) {
      throw new IllegalStateException("Only draft versions can be published");
    }

    status = WorkflowVersionStatus.PUBLISHED;
    publishedAt = Instant.now();
  }
}
