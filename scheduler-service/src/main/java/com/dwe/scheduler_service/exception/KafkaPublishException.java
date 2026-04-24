package com.dwe.scheduler_service.exception;

import org.springframework.http.HttpStatus;

public class KafkaPublishException extends SchedulerBaseException {

    public KafkaPublishException(String topic, String reason) {
        super(
                "Failed to publish message to topic " + topic + " : " + reason,
                "KAFKA_PUBLISH_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}