# Distributed Workflow Engine V2 — Event-Driven Design

**Status:** Proposed  
**Messaging:** Apache Kafka  
**Delivery model:** At-least-once  
**Durable state:** PostgreSQL

## 1. Purpose

Kafka provides asynchronous communication between workflow engine components.

The database remains the source of truth. Kafka events notify components about committed state changes and trigger subsequent processing.

The system must tolerate:

- Duplicate event delivery.
- Consumer restarts.
- Worker crashes.
- Temporary Kafka unavailability.
- Delayed messages.
- Out-of-order event arrival.
- Database transaction failures.

## 2. Event Envelope

Every event must use a common envelope.

Proposed structure:

```json
{
  "eventId": "uuid",
  "eventType": "TASK_READY",
  "schemaVersion": 1,
  "occurredAt": "2026-11-19T17:30:00Z",
  "producer": "scheduler",
  "aggregateType": "TASK_EXECUTION",
  "aggregateId": "uuid",
  "workflowExecutionId": "uuid",
  "correlationId": "uuid",
  "causationId": "uuid",
  "traceId": "trace-id",
  "payload": {}
}
```

Field responsibilities:

| Field | Purpose |
|---|---|
| eventId | Globally unique event identifier |
| eventType | Identifies the event |
| schemaVersion | Event schema version |
| occurredAt | Event creation time |
| producer | Producing component |
| aggregateType | Aggregate category |
| aggregateId | Aggregate identifier |
| workflowExecutionId | Workflow execution context |
| correlationId | Connects events belonging to one execution request |
| causationId | Identifies the event that caused this event |
| traceId | Distributed tracing correlation |
| payload | Event-specific data |

Events must be immutable after publication.

## 3. Initial Event Types

### WorkflowStartedEvent

Published when a workflow execution is created and accepted.

Payload:

- workflowExecutionId
- workflowDefinitionId
- workflowVersionId
- inputData

### TaskReadyEvent

Published when a task becomes eligible for execution.

Payload:

- taskExecutionId
- workflowExecutionId
- taskDefinitionId
- taskKey
- taskType
- attemptNumber
- inputData

### TaskStartedEvent

Published after a worker successfully claims a task.

Payload:

- taskExecutionId
- workflowExecutionId
- workerId
- attemptNumber
- fencingToken
- leaseExpiresAt

### TaskCompletedEvent

Published after a task completes successfully.

Payload:

- taskExecutionId
- workflowExecutionId
- attemptNumber
- outputData

### TaskFailedEvent

Published when a task attempt fails.

Payload:

- taskExecutionId
- workflowExecutionId
- attemptNumber
- errorType
- errorMessage
- retryable

### TaskRetryScheduledEvent

Published when a retry is scheduled.

Payload:

- taskExecutionId
- workflowExecutionId
- failedAttemptNumber
- nextAttemptNumber
- nextRetryAt

### WorkflowCompletedEvent

Published when all required tasks complete successfully.

Payload:

- workflowExecutionId
- completedTaskCount
- totalTaskCount
- outputData

### WorkflowFailedEvent

Published when workflow execution reaches a terminal failed state.

Payload:

- workflowExecutionId
- failedTaskExecutionId
- failureReason

### WorkflowCancelledEvent

Published when a workflow execution has been cancelled.

Payload:

- workflowExecutionId
- cancellationReason

## 4. Kafka Topic Design

Initial topics:

| Topic | Purpose |
|---|---|
| `workflow.events` | Workflow lifecycle events |
| `task.ready` | Tasks eligible for execution |
| `task.events` | Task lifecycle events |
| `workflow.dlq` | Workflow event processing failures |
| `task.dlq` | Task event processing failures |

Topics should use an appropriate replication factor and partition count for the deployment environment.

The task execution identifier should be used as the Kafka key for task-specific events. This preserves partition ordering for events belonging to the same task.

Events belonging to the same workflow can carry a common workflow execution identifier for correlation.

Kafka partition ordering does not guarantee global ordering across topics.

## 5. Transactional Outbox Pattern

Directly updating PostgreSQL and publishing to Kafka in separate operations creates a dual-write problem.

The system will use a transactional outbox.

Producer flow:

1. Begin a database transaction.
2. Update the relevant domain state.
3. Insert the corresponding outbox event.
4. Commit the transaction.
5. Outbox publisher reads pending events.
6. Publisher sends events to Kafka.
7. Publisher marks successfully published events.

If publication succeeds but marking the event as published fails, the event may be published again.

This is expected and must be handled by idempotent consumers.

Multiple outbox publishers must coordinate through database locking, such as `FOR UPDATE SKIP LOCKED`.

## 6. Idempotent Consumer Design

Every consumer must be safe against duplicate event delivery.

Introduce a durable `consumer_inbox` table:

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| consumer_name | VARCHAR(150) | Logical consumer identifier |
| event_id | UUID | Received event identifier |
| processed_at | TIMESTAMPTZ | Processing completion time |

Unique constraint:

`(consumer_name, event_id)`

Consumer processing:

1. Receive event.
2. Begin database transaction.
3. Check whether the event has already been processed.
4. If processed, acknowledge without repeating the operation.
5. Otherwise validate the current aggregate state.
6. Apply the state transition.
7. Record the event in the inbox.
8. Insert any resulting outbox events.
9. Commit the transaction.
10. Acknowledge the Kafka message.

Inbox insertion and domain-state changes must occur in the same transaction.

For operations involving external systems, use an idempotency key derived from the task execution and attempt context where appropriate.

## 7. Worker Dispatch and Ownership

Receiving `TaskReadyEvent` does not grant a worker exclusive ownership of a task.

A worker must:

1. Read the event.
2. Request an atomic claim from the execution database.
3. Verify that the task is still eligible.
4. Transition the task from READY to RUNNING.
5. Create a task attempt.
6. Assign worker ownership and a lease.
7. Increment or assign a fencing token.
8. Commit the claim transaction.
9. Execute the task only after successful claiming.

If the task is no longer eligible, the worker must not execute it.

## 8. Event Failure Handling

Failures must be classified.

### Transient failures

Examples:

- Temporary database unavailability.
- Network interruption.
- Kafka broker unavailability.

These should use bounded retries with backoff.

### Permanent failures

Examples:

- Invalid event schema.
- Unsupported event type.
- Invalid required identifiers.

These should be sent to the appropriate dead-letter topic after the configured handling policy.

### Poison messages

A poison message must not block a partition indefinitely.

Consumers must record processing failures, maintain observability, and route exhausted messages to the DLQ.

DLQ processing and replay must be explicit operational actions.

## 9. Schema Evolution

Event schemas must evolve without unexpectedly breaking existing consumers.

Rules:

1. Every event includes a schema version.
2. Additive optional fields are preferred.
3. Existing field meanings must not change silently.
4. Consumers must handle supported schema versions.
5. Breaking changes require an explicit compatibility strategy.
6. Event contract tests must validate serialization and deserialization.

## 10. Delivery Guarantees

The engine provides at-least-once message delivery with idempotent state transitions.

It does not claim exactly-once execution of arbitrary external side effects.

A task may be attempted again after a crash even if an external operation partially succeeded. Handlers must therefore use idempotency mechanisms when their operations support them.

## 11. Observability

Every event should expose enough information to trace its journey.

Required information:

- eventId
- eventType
- aggregateId
- workflowExecutionId
- correlationId
- causationId
- traceId
- producer
- consumer
- processing duration
- retry count
- failure reason

Metrics should include:

- Outbox publication lag.
- Kafka consumer lag.
- Event processing failures.
- Duplicate event count.
- DLQ message count.
- Task dispatch latency.
- Task execution latency.