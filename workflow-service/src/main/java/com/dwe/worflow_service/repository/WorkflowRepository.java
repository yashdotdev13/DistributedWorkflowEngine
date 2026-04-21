package com.dwe.worflow_service.repository;

import com.dwe.worflow_service.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, String> {

    Optional<Workflow> findByName(String name);

    boolean existsByName(String name);

}
