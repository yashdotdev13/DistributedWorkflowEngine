package com.dwe.worflow_service.controller;

import com.dwe.worflow_service.entity.Task;
import com.dwe.worflow_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {


    private final TaskService taskService;


    @PostMapping("/workflow/{workflowId}")
    public Task createTask(@PathVariable String workflowId,
                           @RequestBody Task task) {

        log.info("API Request: Create Task '{}' for workflow {}", task.getName(), workflowId);

        return taskService.createtask(workflowId, task);
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable String taskId) {

        log.info("API Request: Get Task {}", taskId);
        return taskService.getTaskById(taskId);
    }

    @GetMapping("/workflow/{workflowId}")
    public List<Task> getTasksByWorkflow(@PathVariable String workflowId) {

        log.info("API Request: Get all tasks for workflow {}", workflowId);
        return taskService.getTaskByWorkflow(workflowId);
    }

    @PutMapping("/{taskId}/status")
    public Task updateTaskStatus(@PathVariable String taskId,
                                 @RequestParam String status) {

        log.info("API Request: Update Task {} status to {}", taskId, status);
        return taskService.updateTaskStatus(taskId, status);
    }

    @PutMapping("/{taskId}/retry")
    public void incrementRetry(@PathVariable String taskId) {

        log.info("API Request: Increment retry for task {}", taskId);
        taskService.incrementRetryCount(taskId);
    }
}

