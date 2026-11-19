# Distributed Workflow Engine V2 — Architecture

**Status:** Proposed  
**Version:** 2.0  
**Runtime:** Java 21  
**Framework:** Spring Boot  
**Database:** PostgreSQL  
**Messaging:** Apache Kafka

## 1. Vision

Distributed Workflow Engine V2 is a fault-tolerant, event-driven workflow orchestration platform that executes dependency-based workflows across distributed workers.

The engine will allow users to:

- Define workflows as Directed Acyclic Graphs (DAGs).
- Execute multiple workflow instances independently.
- Run independent tasks concurrently.
- Schedule tasks when their dependencies are satisfied.
- Distribute task execution across multiple workers.
- Retry failed tasks using configurable policies.
- Recover executions after worker crashes.
- Track workflow and task execution history.
- Inspect execution status, results, and failures.

The system will prioritize correctness, durability, extensibility, and observability.

## 2. Architectural Principles

1. **Durable execution:** PostgreSQL is the source of truth for workflow and task execution state.
2. **Event-driven communication:** Kafka transports asynchronous events between engine components.
3. **At-least-once delivery:** Consumers must tolerate duplicate events through idempotent processing.
4. **No false exactly-once guarantees:** External side effects require their own idempotency mechanisms.
5. **Independent executions:** Every workflow execution has its own task execution records and lifecycle.
6. **Concurrency safety:** Task claiming and state transitions must be protected against concurrent schedulers and workers.
7. **Failure recovery:** Expired worker leases and unfinished tasks must be recoverable.
8. **Separation of concerns:** Workflow definitions, execution state, scheduling, and task execution have distinct responsibilities.
9. **Incremental scalability:** Begin with clearly separated modules and deployable components; extract additional services only when justified.
10. **Testability:** Core execution logic must be testable without requiring a running distributed environment.

## 3. High-Level Architecture

The system consists of the following logical components:

### 3.1 Workflow API

Responsibilities:

- Create and manage workflow definitions.
- Validate workflow DAGs.
- Create workflow execution requests.
- Retrieve workflow and task execution status.
- Support execution cancellation and other lifecycle operations.

### 3.2 Workflow Definition Module

Responsibilities:

- Store versioned workflow definitions.
- Store task definitions and dependency relationships.
- Validate unique task identifiers.
- Detect circular dependencies.
- Prevent invalid workflow definitions from being executed.

A workflow definition is immutable once a version has been published. Changes create a new version.

### 3.3 Execution Engine

Responsibilities:

- Create workflow execution instances.
- Create task execution records for each workflow execution.
- Maintain workflow execution lifecycle.
- Coordinate execution state transitions.
- Determine when a workflow has completed or failed.

A workflow definition and a workflow execution are separate concepts.

### 3.4 Scheduler

Responsibilities:

- Identify tasks whose dependencies have completed successfully.
- Atomically claim eligible tasks.
- Dispatch task-ready events.
- Prevent multiple schedulers from incorrectly claiming the same task.
- Respect concurrency limits and retry scheduling policies.

The scheduler must not depend on in-memory state for durable scheduling decisions.

### 3.5 Worker Runtime

Responsibilities:

- Consume task-ready events.
- Claim and execute assigned tasks.
- Maintain worker leases and heartbeats.
- Record task results and failures.
- Publish task completion events.
- Support multiple task handler implementations.

Workers must be horizontally scalable.

### 3.6 Recovery Engine

Responsibilities:

- Detect expired task leases.
- Identify tasks abandoned by failed workers.
- Recover eligible tasks.
- Apply retry policies and backoff.
- Mark tasks permanently failed when retry limits are exceeded.
- Reconcile incomplete executions.

### 3.7 Event Publisher

Responsibilities:

- Persist outgoing events using the transactional outbox pattern.
- Publish committed outbox events to Kafka.
- Track publication state.
- Prevent database/message dual-write inconsistencies.

### 3.8 Observability

Responsibilities:

- Expose application health and metrics.
- Provide distributed tracing.
- Record structured logs.
- Track workflow and task execution latency.
- Expose operational metrics such as queue lag, retries, and worker availability.

## 4. High-Level System Flow

1. A client submits a workflow execution request.
2. Workflow API validates the requested workflow version.
3. Execution Engine creates a workflow execution and its task execution records.
4. Scheduler identifies tasks with no unfinished dependencies.
5. Scheduler atomically claims eligible tasks.
6. Task-ready events are persisted through the outbox and published to Kafka.
7. Available workers consume task-ready events.
8. A worker claims the task, records its attempt, and executes the registered handler.
9. Worker persists the result and task state.
10. Task completion events are published.
11. Scheduler evaluates downstream dependencies.
12. The process continues until all tasks reach terminal states.
13. Execution Engine updates the workflow execution status.

## 5. Logical Module Structure

The initial implementation will use a Maven multi-module architecture.

Planned modules:

| Module | Responsibility |
|---|---|
| `workflow-api` | REST API and request validation |
| `workflow-definition` | Workflow definitions and DAG validation |
| `execution-engine` | Workflow and task execution lifecycle |
| `scheduler` | Dependency evaluation and task dispatch |
| `worker-runtime` | Task consumption and execution |
| `task-sdk` | Task handler API and execution context |
| `event-contracts` | Shared event schemas and contracts |
| `event-publisher` | Transactional outbox and Kafka publishing |
| `recovery-engine` | Lease expiration and execution recovery |
| `execution-history` | Execution history and audit records |
| `observability` | Metrics, tracing, and instrumentation |

These are logical boundaries, not a commitment to deploy every module as a separate microservice.

## 6. Technology Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Application framework | Spring Boot |
| Build system | Maven |
| API | Spring Web |
| Persistence | Spring Data JPA |
| Database | PostgreSQL |
| Database migrations | Flyway |
| Messaging | Apache Kafka |
| Cache / ephemeral coordination | Redis, when justified |
| Testing | JUnit 5, Mockito |
| Integration testing | Testcontainers |
| Containers | Docker and Docker Compose |
| Metrics | Micrometer and Prometheus |
| Dashboards | Grafana |
| Distributed tracing | OpenTelemetry |
| API documentation | OpenAPI |

PostgreSQL remains the durable source of truth. Redis will not be used as the primary execution-state store.

## 7. Core Domain Concepts

### Workflow Definition

A versioned description of a workflow, including its task definitions and dependencies.

### Task Definition

A reusable description of a task, including its task type, configuration, and retry policy.

### Workflow Execution

One particular run of a workflow definition.

Multiple executions of the same workflow definition must be supported concurrently.

### Task Execution

One task's execution state within a particular workflow execution.

A task definition must never be used as the storage location for runtime execution state.

### Task Attempt

An individual attempt to execute a task. Each retry creates a new attempt record.

### Task Handler

A registered implementation that performs the actual task operation.

The initial implementation will support trusted built-in handlers rather than arbitrary remote code execution.

## 8. Reliability and Consistency

The system must implement:

- Transactional outbox for reliable event publication.
- Idempotent event consumers.
- Atomic task claiming.
- Explicit task state transitions.
- Worker leases and heartbeat tracking.
- Configurable retry policies.
- Exponential backoff.
- Timeout handling.
- Recovery of expired executions.
- Durable execution history.
- Failure propagation through workflow dependencies.

Database state transitions and event publication must be designed together to avoid inconsistent execution state.

## 9. Initial Scope

The first functional version will support:

1. Workflow creation and versioning.
2. DAG validation.
3. Workflow execution creation.
4. Dependency-based task scheduling.
5. Kafka-based task dispatch.
6. Multiple workers.
7. Task execution and result persistence.
8. Task retry and failure handling.
9. Worker crash recovery.
10. Workflow and task status APIs.

Advanced features such as a visual workflow editor, distributed workflow compensation, multi-tenancy, and arbitrary user-supplied code execution are outside the initial implementation scope.

## 10. Definition of Done

The architecture will be considered successfully implemented when:

- A client can create a valid workflow DAG.
- Invalid cyclic workflows are rejected.
- A workflow can be executed multiple times independently.
- Independent tasks can execute concurrently.
- Dependent tasks execute only after their prerequisites succeed.
- Multiple scheduler instances cannot incorrectly claim the same task.
- Multiple worker instances can execute tasks.
- Duplicate Kafka events do not cause unintended duplicate execution.
- Worker crashes can be recovered through lease expiration.
- Failed tasks follow configured retry policies.
- Every execution and attempt has durable history.
- The final workflow state is consistent with its task states.
- Integration tests validate the complete execution lifecycle.