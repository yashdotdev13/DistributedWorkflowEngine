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

@Entity
@Table(
    name = "task_dependencies",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_task_dependency_edge",
          columnNames = {"predecessor_task_id", "successor_task_id"})
    })
public class TaskDependencyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "workflow_version_id", nullable = false)
  private WorkflowDefinitionVersionEntity workflowVersion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "predecessor_task_id", nullable = false)
  private TaskDefinitionEntity predecessorTask;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "successor_task_id", nullable = false)
  private TaskDefinitionEntity successorTask;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected TaskDependencyEntity() {}

  public TaskDependencyEntity(
      WorkflowDefinitionVersionEntity workflowVersion,
      TaskDefinitionEntity predecessorTask,
      TaskDefinitionEntity successorTask) {
    this.workflowVersion = workflowVersion;
    this.predecessorTask = predecessorTask;
    this.successorTask = successorTask;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public WorkflowDefinitionVersionEntity getWorkflowVersion() {
    return workflowVersion;
  }

  public TaskDefinitionEntity getPredecessorTask() {
    return predecessorTask;
  }

  public TaskDefinitionEntity getSuccessorTask() {
    return successorTask;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
