package com.dwe.worflow_service.service;

import com.dwe.worflow_service.entity.Task;

import java.util.List;

public interface TaskService {

    Task createtask(String workflowId, Task task);

    Task getTaskById(String taskId);

    List<Task> getTaskByWorkflow(String workflowId);

    Task updateTaskStatus(String taskId, String status);

    void incrementRetryCount(String taskId);
}
