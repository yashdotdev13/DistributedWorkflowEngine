package com.dwe.worker_service.exception;


import org.springframework.http.HttpStatus;

public class WorkerKafkaPublishException extends WorkerBaseException {

    public WorkerKafkaPublishException(String topic, String reason) {
        super(
                "Failed to publish event to topic " + topic + " : " + reason,
                "KAFKA_PUBLISH_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}