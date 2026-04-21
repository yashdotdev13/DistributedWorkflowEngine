package com.dwe.worflow_service.service;

import com.dwe.worflow_service.entity.TaskDependency;

import java.util.List;

public interface TaskDependencyService {

    TaskDependency createDependency(String parentTaskId, String childTaskId);
    List<TaskDependency> getDependenciesByTask(String taskId);

    void validateDAG(String workflowId);
}
