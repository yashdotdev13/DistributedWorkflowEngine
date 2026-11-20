package com.dwe.persistence.repository;

import com.dwe.persistence.entity.TaskDefinitionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinitionEntity, UUID> {

  List<TaskDefinitionEntity> findAllByWorkflowVersion_Id(UUID workflowVersionId);

  Optional<TaskDefinitionEntity> findByWorkflowVersion_IdAndTaskKey(
      UUID workflowVersionId, String taskKey);

  boolean existsByWorkflowVersion_IdAndTaskKey(UUID workflowVersionId, String taskKey);

  List<TaskDefinitionEntity> findAllByWorkflowVersion_IdOrderByTaskKeyAsc(UUID workflowVersionId);

  long countByWorkflowVersion_Id(UUID workflowVersionId);
}
