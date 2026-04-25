package com.dwe.scheduler_service.kafka;

import com.dwe.events.WorkflowStartedEvent;
import com.dwe.scheduler_service.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowStartedConsumer {

    private final SchedulerService schedulerService;

    @KafkaListener(
            topics = "workflow-started",
            groupId = "scheduler-group",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void consumeWorkflowStarted(WorkflowStartedEvent event){

        log.info("Received workflowStartedEvent: workflowId={}, name={}",
                event.getWorkflowId(),
                event.getWorkflowName());
        try {
            schedulerService.scheduleWorkflow(event.getWorkflowId());

        } catch (Exception ex) {

            log.error("Error processing workflow event for workflowId={}",
                    event.getWorkflowId(), ex);

            // later: dead-letter queue
        }
    }
}