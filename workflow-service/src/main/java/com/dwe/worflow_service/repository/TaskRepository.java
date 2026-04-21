package com.dwe.worflow_service.repository;

import com.dwe.worflow_service.entity.Task;
import com.dwe.worflow_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("""
        SELECT t FROM Task t
        WHERE t.status = 'PENDING'
        AND NOT EXISTS (
            SELECT td FROM TaskDependency td
            WHERE td.childTask.id = t.id
            AND td.parentTask.status != 'SUCCESS'
        )
    """)
    List<Task> findExecutableTasks();

    // Tasks for retry
    List<Task> findByStatusAndRetryCountLessThan(TaskStatus status, Integer maxRetries);
}
