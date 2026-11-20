package com.dwe.persistence.repository;

import com.dwe.persistence.entity.WorkflowDefinitionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionRepository
    extends JpaRepository<WorkflowDefinitionEntity, UUID> {

  boolean existsByName(String name);
}
