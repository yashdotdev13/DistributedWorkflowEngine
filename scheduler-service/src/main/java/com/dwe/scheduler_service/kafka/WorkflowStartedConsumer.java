package com.dwe.scheduler_service.kafka;

import com.dwe.scheduler_service.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowStartedConsumer {

    private final SchedulerService schedulerService;
}
