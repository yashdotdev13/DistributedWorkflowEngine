package com.dwe.worflow_service.repository;

import com.dwe.worflow_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
}
