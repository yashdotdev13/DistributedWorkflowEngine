package com.dwe.worker_service.kafka;




import com.dwe.events.TaskReadyEvent;
import com.dwe.worker_service.service.TaskExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReadyConsumer {

    private final TaskExecutionService taskExecutionService;

    @KafkaListener(
            topics = "task-ready",
            groupId = "worker-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTaskReady(TaskReadyEvent event) {

        log.info("Received TaskReadyEvent: taskId={}, workflowId={}",
                event.getTaskId(),
                event.getWorkflowId());

        try {
            taskExecutionService.executeTask(event);

        } catch (Exception ex) {

            log.error("Error executing taskId={}", event.getTaskId(), ex);

            // later: dead-letter queue / retry
        }
    }
}