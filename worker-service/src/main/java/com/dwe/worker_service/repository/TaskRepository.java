package com.dwe.worker_service.repository;


import com.dwe.worker_service.entity.Task;
import com.dwe.worker_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    Optional<Task> findById(String taskId);

    // Optional (future use)
    Optional<Task> findByIdAndStatus(String taskId, TaskStatus status);
}