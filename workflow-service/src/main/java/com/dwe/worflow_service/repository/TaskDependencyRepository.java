package com.dwe.worflow_service.repository;

import com.dwe.worflow_service.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
}
