package com.dwe.worflow_service.service.Impl;

import com.dwe.worflow_service.entity.Task;
import com.dwe.worflow_service.entity.Workflow;
import com.dwe.worflow_service.enums.TaskStatus;
import com.dwe.worflow_service.exceptions.TaskNotFoundException;
import com.dwe.worflow_service.exceptions.WorkflowNotFoundException;
import com.dwe.worflow_service.repository.TaskRepository;
import com.dwe.worflow_service.repository.WorkflowRepository;
import com.dwe.worflow_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;

    @Override
    @Transactional
    public Task createtask(String workflowId, Task task) {

        log.info("Creating Task '{}' for workflow: {}",task.getName(), workflowId);

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(()->{
                    log.error("Workflow not found with while,creating task: {}",workflowId);
                    return new WorkflowNotFoundException(workflowId);
                });
        //set defaults
        task.setWorkflow(workflow);
        task.setStatus(TaskStatus.PENDING);
        task.setRetryCount(0);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        log.info("Task created successfully with id: {}",savedTask.getId());

        return savedTask;
    }

    @Override
    public Task getTaskById(String taskId) {

        log.info("Fetching task with id: {}",taskId);

        return taskRepository.findById(taskId)
                .orElseThrow(()->{
                    log.error("Task not found with id: {}",taskId);
                    return new TaskNotFoundException(taskId);
                });
    }


    // get All tasks of a workflow

    @Override
    public List<Task> getTaskByWorkflow(String workflowId) {


        log.info("Fetching tasks for workflow: {}",workflowId);

        // validate workflow exists
        if(!workflowRepository.existsById(workflowId)){
            log.error("Workflow not found with while,fetching tasks: {}",workflowId);
            throw new WorkflowNotFoundException(workflowId);
        }

        List<Task> tasks = taskRepository.findByWorkflowId(workflowId);
        log.info("Total tasks found for workflow {}: {}",workflowId, tasks.size());

        return tasks;

    }

    // update task status

    @Override
    @Transactional
    public Task updateTaskStatus(String taskId, String status) {

        log.info("Updating status for task {} to {}", taskId, status);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()->{
                    log.error("Task not found with id: {}",taskId);
                    return new TaskNotFoundException(taskId);
                });

        TaskStatus newStatus;

        try{
            newStatus = TaskStatus.valueOf(status.toUpperCase());
        }catch(IllegalArgumentException e){
            log.error("Invalid task status providee: {}",status);
            throw new IllegalArgumentException("Invalid task status: "+status);
        }

       // enforce valid state transitions
        validateStateTransition(task.getStatus(), newStatus);

        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        log.info("Task {} status updated to {}", taskId, newStatus);

        return updatedTask;
    }

    @Override
    @Transactional
    public void incrementRetryCount(String taskId) {

        log.info("Incrementing retry count for task: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found while incrementing retry: {}", taskId);
                    return new TaskNotFoundException(taskId);
                });

        int currentRetry = task.getRetryCount() == null ? 0 : task.getRetryCount();
        int maxRetries = task.getMaxRetries() == null ? 0 : task.getMaxRetries();

        if (currentRetry >= maxRetries) {
            log.warn("Max retries reached for task: {}", taskId);
            return;
        }

        task.setRetryCount(currentRetry + 1);
        task.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(task);
        log.info("Retry count updated for task {}: {}", taskId, task.getRetryCount());
    }

    /**
     * Validate state transitions (IMPORTANT )
     */
    private void validateStateTransition(TaskStatus current, TaskStatus next) {

        if (current == TaskStatus.SUCCESS) {
            throw new IllegalStateException("Cannot change state from SUCCESS");
        }

        if (current == TaskStatus.RUNNING && next == TaskStatus.PENDING) {
            throw new IllegalStateException("Invalid transition RUNNING -> PENDING");
        }
    }
}
