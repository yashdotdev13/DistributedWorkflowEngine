package com.dwe.persistence.repository;

import com.dwe.persistence.entity.WorkflowDefinitionVersionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionVersionRepository
    extends JpaRepository<WorkflowDefinitionVersionEntity, UUID> {

  List<WorkflowDefinitionVersionEntity> findAllByWorkflowDefinition_IdOrderByVersionDesc(
      UUID workflowDefinitionId);

  Optional<WorkflowDefinitionVersionEntity> findByWorkflowDefinition_IdAndVersion(
      UUID workflowDefinitionId, Integer version);

  boolean existsByWorkflowDefinition_IdAndVersion(UUID workflowDefinitionId, Integer version);

  Optional<WorkflowDefinitionVersionEntity> findFirstByWorkflowDefinition_IdOrderByVersionDesc(
      UUID workflowDefinitionId);
}
