package com.dwe.worflow_service.controller;


import com.dwe.worflow_service.entity.TaskDependency;
import com.dwe.worflow_service.service.TaskDependencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dependencies")
@RequiredArgsConstructor
@Slf4j
public class TaskDependencyController {

    private final TaskDependencyService dependencyService;

    @PostMapping
    public TaskDependency createDependency(@RequestParam String parentTaskId,
                                           @RequestParam String childTaskId) {

        log.info("API Request: Create dependency {} -> {}", parentTaskId, childTaskId);
        return dependencyService.createDependency(parentTaskId, childTaskId);
    }


    @GetMapping("/task/{taskId}")
    public List<TaskDependency> getDependencies(@PathVariable String taskId) {

        log.info("API Request: Get dependencies for task {}", taskId);
        return dependencyService.getDependenciesByTask(taskId);
    }

    @PostMapping("/validate/{workflowId}")
    public void validateDAG(@PathVariable String workflowId) {

        log.info("API Request: Validate DAG for workflow {}", workflowId);
        dependencyService.validateDAG(workflowId);
    }
}
