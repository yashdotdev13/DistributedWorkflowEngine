package com.dwe.worflow_service.service.Impl;

import com.dwe.events.WorkflowStartedEvent;
import com.dwe.worflow_service.entity.Workflow;
import com.dwe.worflow_service.enums.WorkflowStatus;
import com.dwe.worflow_service.exceptions.DuplicateWorkflowException;
import com.dwe.worflow_service.exceptions.WorkflowNotFoundException;
import com.dwe.worflow_service.kafka.WorkflowEventProducer;
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
    private final WorkflowEventProducer eventProducer;

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

        log.info("Fetching workflow with id: {}",workflowId);
        return workflowRepository.findById(workflowId)
                .orElseThrow(()->{
                    log.error("workflow not found with id: {}",workflowId);
                    return new WorkflowNotFoundException(workflowId);
                });
    }

    @Override
    public List<Workflow> getWorkflows() {

        log.info("Fetching all workflows");

        List<Workflow> workflows = workflowRepository.findAll();
        log.info("Total workflows found: {}",workflows.size());
        return workflows;
    }

    @Override
    @Transactional
    public void deleteWorkflow(String workflowId) {

        log.info("Deleting workflow with id: {}",workflowId);

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(()->{
                    log.error("Workflow not found for deletion: {}",workflowId);
                    return new WorkflowNotFoundException(workflowId);
                });

        workflowRepository.delete(workflow);

        log.info("Workflow deleted successfully with id: {}",workflowId);

    }

    @Override
    @Transactional
    public void startWorkflow(String workflowId) {


        log.info("Starting workflow with id: {}",workflowId);

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(()-> {
                    log.error("Workflow not found for execution: {}",workflowId);
                    return new WorkflowNotFoundException(workflowId);
                });

        workflow.setStatus(WorkflowStatus.RUNNING);
        workflow.setUpdatedAt(LocalDateTime.now());

        workflowRepository.save(workflow);

        log.info("Workflow marked as RUNNING: {}",workflowId);


        // Kafka publisher event
        //  Create event using common-lib
        WorkflowStartedEvent event = WorkflowStartedEvent.builder()
                .workflowId(workflow.getId())
                .workflowName(workflow.getName())
                .build();

        eventProducer.publishWorkflowStarted(event);
    }
}
