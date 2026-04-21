package com.dwe.worflow_service.service.Impl;

import com.dwe.worflow_service.entity.Workflow;
import com.dwe.worflow_service.enums.WorkflowStatus;
import com.dwe.worflow_service.exceptions.DuplicateWorkflowException;
import com.dwe.worflow_service.repository.WorkflowRepository;
import com.dwe.worflow_service.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Override
    @Transactional
    public Workflow createWorkflow(Workflow workflow) {

        log.info("Creating workflow with name: {}",workflow.getName());

        if(workflowRepository.existsByName(workflow.getName())){
            log.error("Workflow already exists with name: {}",workflow.getName());
            throw new DuplicateWorkflowException(workflow.getName());
        }

        workflow.setStatus(WorkflowStatus.CREATED);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());

        Workflow savedWorkflow = workflowRepository.save(workflow);

        log.info("Workflow created successfully with id: {}", savedWorkflow.getId());

        return savedWorkflow;
    }

    @Override
    public Workflow getWorkflowById(String workflowId) {
        return null;
    }

    @Override
    public List<Workflow> getWorkflows() {
        return List.of();
    }

    @Override
    public void deleteWorkflow(String workflowId) {

    }

    @Override
    public void startWorkflow(String workflowId) {

    }
}
