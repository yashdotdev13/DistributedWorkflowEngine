package com.dwe.persistence.repository;

import com.dwe.persistence.entity.TaskDependencyEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDependencyRepository extends JpaRepository<TaskDependencyEntity, UUID> {

  List<TaskDependencyEntity> findAllByWorkflowVersion_Id(UUID workflowVersionId);

  List<TaskDependencyEntity> findAllByPredecessorTask_Id(UUID predecessorTaskId);

  List<TaskDependencyEntity> findAllBySuccessorTask_Id(UUID successorTaskId);

  boolean existsByPredecessorTask_IdAndSuccessorTask_Id(
      UUID predecessorTaskId, UUID successorTaskId);

  long countByWorkflowVersion_Id(UUID workflowVersionId);

  void deleteAllByWorkflowVersion_Id(UUID workflowVersionId);
}
