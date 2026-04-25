package com.dwe.scheduler_service.service.Impl;



import com.dwe.scheduler_service.entities.Task;
import com.dwe.scheduler_service.enums.TaskStatus;
import com.dwe.scheduler_service.repository.TaskRepository;
import com.dwe.scheduler_service.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void scheduleWorkflow(String workflowId) {

        log.info("Scheduling workflow execution for workflowId={}", workflowId);


        List<Task> tasks = taskRepository.findExecutableTasksByWorkflow(workflowId);

        if (tasks.isEmpty()) {
            log.warn("No executable tasks found for workflowId={}", workflowId);
            return;
        }

        log.info("Found {} executable tasks for workflowId={}", tasks.size(), workflowId);
        for (Task task : tasks) {

            try {
                task.setStatus(TaskStatus.READY);
                taskRepository.save(task);

                log.info("Task marked as READY: taskId={}", task.getId());

            } catch (Exception ex) {
                log.error("Error updating task status for taskId={}", task.getId(), ex);
            }
        }
        log.info("Scheduling step completed for workflowId={}", workflowId);
    }
}