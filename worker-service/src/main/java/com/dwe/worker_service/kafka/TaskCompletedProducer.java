package com.dwe.worker_service.kafka;


import com.dwe.events.TaskCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCompletedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "task-completed";

    public void publish(TaskCompletedEvent event) {

        log.info("Publishing TaskCompletedEvent for taskId={}", event.getTaskId());
        kafkaTemplate.send(TOPIC, event.getTaskId(), event);
        log.info("Published to topic {}", TOPIC);
    }
}
