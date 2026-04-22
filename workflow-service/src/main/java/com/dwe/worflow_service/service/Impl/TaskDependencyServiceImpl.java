package com.dwe.worflow_service.service.Impl;

import com.dwe.worflow_service.entity.Task;
import com.dwe.worflow_service.entity.TaskDependency;
import com.dwe.worflow_service.exceptions.InvalidDAGException;
import com.dwe.worflow_service.exceptions.TaskNotFoundException;
import com.dwe.worflow_service.repository.TaskDependencyRepository;
import com.dwe.worflow_service.repository.TaskRepository;
import com.dwe.worflow_service.service.TaskDependencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TaskDependencyServiceImpl implements TaskDependencyService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;



    @Override
    @Transactional
    public TaskDependency createDependency(String parentTaskId, String childTaskId) {

        log.info("Creating dependency: {} -> {}", parentTaskId, childTaskId);

        if (parentTaskId.equals(childTaskId)) {
            log.error("Self dependency detected for task: {}", parentTaskId);
            throw new InvalidDAGException("Task cannot depend on itself");
        }

        Task parent = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> {
                    log.error("Parent task not found: {}", parentTaskId);
                    return new TaskNotFoundException(parentTaskId);
                });

        Task child = taskRepository.findById(childTaskId)
                .orElseThrow(() -> {
                    log.error("Child task not found: {}", childTaskId);
                    return new TaskNotFoundException(childTaskId);
                });

        // Check if adding this creates a cycle
        if (createsCycle(parentTaskId, childTaskId)) {
            log.error("Cycle detected while adding dependency {} -> {}", parentTaskId, childTaskId);
            throw new InvalidDAGException("Adding this dependency will create a cycle");
        }

        TaskDependency dependency = TaskDependency.builder()
                .parentTask(parent)
                .childTask(child)
                .build();

        TaskDependency saved = taskDependencyRepository.save(dependency);

        log.info("Dependency created successfully: {} -> {}", parentTaskId, childTaskId);

        return saved;
    }

    @Override
    public List<TaskDependency> getDependenciesByTask(String taskId) {

        log.info("Fetching dependencies for task: {}", taskId);

        if (!taskRepository.existsById(taskId)) {
            log.error("Task not found while fetching dependencies: {}", taskId);
            throw new TaskNotFoundException(taskId);
        }

        return taskDependencyRepository.findByChildTaskId(taskId);
    }

    @Override
    public void validateDAG(String workflowId) {

        log.info("Validating DAG for workflow: {}", workflowId);

        List<Task> tasks = taskRepository.findByWorkflowId(workflowId);

        Map<String, List<String>> graph = new HashMap<>();

        for (Task task : tasks) {
            graph.put(task.getId(), new ArrayList<>());
        }

        List<TaskDependency> dependencies = taskDependencyRepository.findAll();

        for (TaskDependency dep : dependencies) {
            String parentId = dep.getParentTask().getId();
            String childId = dep.getChildTask().getId();

            graph.get(parentId).add(childId);
        }

        //  Detect cycles using DFS
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String node : graph.keySet()) {
            if (detectCycle(node, graph, visited, recursionStack)) {
                log.error("Cycle detected in workflow: {}", workflowId);
                throw new InvalidDAGException("Workflow contains a cycle");
            }
        }

        log.info("DAG validation successful for workflow: {}", workflowId);
    }



    private boolean createsCycle(String parentId, String childId) {

        // Build graph
        Map<String, List<String>> graph = new HashMap<>();

        List<TaskDependency> dependencies = taskDependencyRepository.findAll();

        for (TaskDependency dep : dependencies) {
            String p = dep.getParentTask().getId();
            String c = dep.getChildTask().getId();

            graph.computeIfAbsent(p, k -> new ArrayList<>()).add(c);
        }

        // Add new edge temporarily
        graph.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childId);

        return detectCycle(parentId, graph, new HashSet<>(), new HashSet<>());
    }

    private boolean detectCycle(String node,
                                Map<String, List<String>> graph,
                                Set<String> visited,
                                Set<String> recursionStack) {

        if (recursionStack.contains(node)) {
            return true;
        }

        if (visited.contains(node)) {
            return false;
        }

        visited.add(node);
        recursionStack.add(node);

        for (String neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (detectCycle(neighbor, graph, visited, recursionStack)) {
                return true;
            }
        }

        recursionStack.remove(node);

        return false;
    }
}
