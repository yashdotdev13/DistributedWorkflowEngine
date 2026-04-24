package com.dwe.worflow_service.kafka;


import com.dwe.events.WorkflowStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "workflow-started";

    public void publishWorkflowStarted(WorkflowStartedEvent event){

        log.info("Publishing workflowStartedEvent for workflowId={}",event.getWorkflowId());
        kafkaTemplate.send(TOPIC, event.getWorkflowId(), event);

        log.info("Event published successfully to topic {}",TOPIC);
    }

}
