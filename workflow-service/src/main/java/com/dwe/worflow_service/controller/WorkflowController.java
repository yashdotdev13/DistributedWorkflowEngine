package com.dwe.worflow_service.controller;


import com.dwe.worflow_service.entity.Workflow;
import com.dwe.worflow_service.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public Workflow createWorkflow(@RequestBody Workflow workflow) {

        log.info("API Request: Create Workflow - {}", workflow.getName());
        return workflowService.createWorkflow(workflow);
    }

    @GetMapping("/{workflowId}")
    public Workflow getWorkflow(@PathVariable String workflowId) {

        log.info("API Request: Get Workflow - {}", workflowId);
        return workflowService.getWorkflowById(workflowId);
    }

    @GetMapping
    public List<Workflow> getAllWorkflows() {

        log.info("API Request: Get All Workflows");
        return workflowService.getWorkflows();
    }

    @DeleteMapping("/{workflowId}")
    public void deleteWorkflow(@PathVariable String workflowId) {

        log.info("API Request: Delete Workflow - {}", workflowId);
        workflowService.deleteWorkflow(workflowId);
    }

    @PostMapping("/{workflowId}/start")
    public void startWorkflow(@PathVariable String workflowId) {

        log.info("API Request: Start Workflow - {}", workflowId);
        workflowService.startWorkflow(workflowId);
    }


}
