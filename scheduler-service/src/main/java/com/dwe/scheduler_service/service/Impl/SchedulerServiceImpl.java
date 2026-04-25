package com.dwe.scheduler_service.service.Impl;


import com.dwe.scheduler_service.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    @Override
    public void scheduleWorkflow(String workflowId) {

        log.info("Scheduling workflow execution for workflowId={}", workflowId);

        // 🔥 NEXT STEP:
        // 1. Fetch executable tasks
        // 2. Publish to task-ready topic
    }
}