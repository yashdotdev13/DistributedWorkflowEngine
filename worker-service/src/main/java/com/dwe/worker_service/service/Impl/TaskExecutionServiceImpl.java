package com.dwe.worker_service.service.Impl;


import com.dwe.events.TaskCompletedEvent;
import com.dwe.events.TaskReadyEvent;
import com.dwe.worker_service.entity.Task;
import com.dwe.worker_service.enums.TaskStatus;
import com.dwe.worker_service.exception.WorkerTaskNotFoundException;
import com.dwe.worker_service.kafka.TaskCompletedProducer;
import com.dwe.worker_service.repository.TaskRepository;
import com.dwe.worker_service.service.TaskExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final TaskRepository taskRepository;
    private final TaskCompletedProducer taskCompletedProducer;

    @Override
    public void executeTask(TaskReadyEvent event) {

        log.info("Starting execution for taskId={}", event.getTaskId());

        Task task = taskRepository.findById(event.getTaskId())
                .orElseThrow(() -> new WorkerTaskNotFoundException(event.getTaskId()));

        try {
            task.setStatus(TaskStatus.RUNNING);
            taskRepository.save(task);

            log.info("Task is now RUNNING: {}", task.getId());

            simulateExecution(task);

            task.setStatus(TaskStatus.SUCCESS);
            taskRepository.save(task);

            TaskCompletedEvent event1 = TaskCompletedEvent.builder()
                    .taskId(task.getId())
                    .workflowId(task.getWorkflow().getId())
                    .status("SUCCESS")
                    .result("Task executed successfully")
                    .build();

            taskCompletedProducer.publish(event1);

            log.info("Task completed SUCCESS: {}", task.getId());

        } catch (Exception ex) {

            log.error("Task execution failed for taskId={}", task.getId(), ex);

            task.setStatus(TaskStatus.FAILED);
            taskRepository.save(task);

            TaskCompletedEvent event1 = TaskCompletedEvent.builder()
                    .taskId(task.getId())
                    .workflowId(task.getWorkflow().getId())
                    .status("FAILED")
                    .result("Execution failed")
                    .build();

            taskCompletedProducer.publish(event1);
        }
    }

    private void simulateExecution(Task task) {

        log.info("Executing task logic: {}", task.getName());

        try {
            Thread.sleep(1000); // simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}