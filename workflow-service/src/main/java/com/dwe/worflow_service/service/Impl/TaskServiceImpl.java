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



    @Override
    public List<Task> getTaskByWorkflow(String workflowId) {
        return List.of();
    }

    @Override
    public Task updateTaskStatus(String taskId, String status) {
        return null;
    }

    @Override
    public void incrementRetryCount(String taskId) {

    }
}
