package com.dwe.scheduler_service.kafka;



import com.dwe.events.TaskReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "task-ready";

    public void publishTaskReady(TaskReadyEvent event) {

        log.info("Publishing TaskReadyEvent for taskId={}", event.getTaskId());

        kafkaTemplate.send(TOPIC, event.getTaskId(), event);

        log.info("TaskReadyEvent published to topic {}", TOPIC);
    }
}
