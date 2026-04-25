package com.dwe.scheduler_service.repository;


import com.dwe.scheduler_service.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, String> {
}