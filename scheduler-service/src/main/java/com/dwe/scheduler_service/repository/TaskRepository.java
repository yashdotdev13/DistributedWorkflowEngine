package com.dwe.scheduler_service.repository;



import com.dwe.scheduler_service.entities.Task;
import com.dwe.scheduler_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {


    List<Task> findByWorkflowId(String workflowId);


    @Query("""
        SELECT t FROM Task t
        WHERE t.workflow.id = :workflowId
        AND t.status = 'PENDING'
        AND NOT EXISTS (
            SELECT td FROM TaskDependency td
            WHERE td.childTask.id = t.id
            AND td.parentTask.status != 'SUCCESS'
        )
    """)
    List<Task> findExecutableTasksByWorkflow(String workflowId);

    List<Task> findByStatus(TaskStatus status);
}