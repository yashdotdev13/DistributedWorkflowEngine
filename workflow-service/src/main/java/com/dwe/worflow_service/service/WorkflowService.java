package com.dwe.worflow_service.service;

import com.dwe.worflow_service.entity.Workflow;

import java.util.List;

public interface WorkflowService {

    Workflow createWorkflow(Workflow workflow);

    Workflow getWorkflowById(String workflowId);

    List<Workflow> getWorkflows();

    void deleteWorkflow(String workflowId);

    void startWorkflow(String workflowId);
}
