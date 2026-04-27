package com.dwe.worker_service.service;

import com.dwe.events.TaskReadyEvent;

public interface  TaskExecutionService {

    void executeTask(TaskReadyEvent event);
}
