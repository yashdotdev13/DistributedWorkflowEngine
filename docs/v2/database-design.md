# Distributed Workflow Engine V2 — Database Design

**Status:** Proposed  
**Database:** PostgreSQL  
**Schema versioning:** Flyway  
**Primary principle:** PostgreSQL is the source of truth for all durable workflow and execution state.

## 1. Design Goals

The database must support:

- Versioned workflow definitions.
- Multiple independent executions of the same workflow.
- DAG-based task dependencies.
- Concurrent task scheduling.
- Multiple execution attempts per task.
- Atomic task claiming.
- Worker lease and heartbeat tracking.
- Retry scheduling and failure recovery.
- Reliable event publication through the transactional outbox.
- Complete execution history and auditability.

## 2. Entity Relationship Overview

The primary relationships are:

```text
workflow_definitions
       |
       | 1:N
       v
workflow_definition_versions
       |
       | 1:N
       v
task_definitions
       |
       | N:M through
       v
task_dependencies

workflow_definition_versions
       |
       | 1:N
       v
workflow_executions
       |
       | 1:N
       v
task_executions
       |
       | 1:N
       v
task_attempts

workflow_executions
       |
       | 1:N
       v
execution_events

outbox_events
```

The dependency relationships between task definitions are copied or resolved into the execution-specific task graph when a workflow execution is created. This ensures that an existing execution remains tied to its original workflow version.

## 3. Core Tables

### 3.1 workflow_definitions

Stores the identity and metadata of a workflow.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| name | VARCHAR(255) | Workflow name |
| description | TEXT | Optional description |
| created_at | TIMESTAMPTZ | Creation time |
| updated_at | TIMESTAMPTZ | Last update time |

### 3.2 workflow_definition_versions

Stores immutable versions of workflow definitions.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_definition_id | UUID | Parent workflow |
| version | INTEGER | Version number |
| status | VARCHAR(30) | DRAFT or PUBLISHED |
| created_at | TIMESTAMPTZ | Creation time |
| published_at | TIMESTAMPTZ | Publication time |

Constraints:

- Unique `(workflow_definition_id, version)`.
- Foreign key to `workflow_definitions`.
- Published versions cannot be modified.

### 3.3 task_definitions

Stores reusable task definitions belonging to a workflow version.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_version_id | UUID | Workflow version |
| task_key | VARCHAR(255) | Unique task identifier within version |
| task_type | VARCHAR(100) | Registered handler type |
| configuration | JSONB | Task configuration |
| max_retries | INTEGER | Maximum retry count |
| timeout_seconds | INTEGER | Execution timeout |
| created_at | TIMESTAMPTZ | Creation time |

Constraints:

- Unique `(workflow_version_id, task_key)`.
- `max_retries >= 0`.
- `timeout_seconds > 0`.

### 3.4 task_dependencies

Stores DAG dependency relationships.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_version_id | UUID | Workflow version |
| predecessor_task_id | UUID | Upstream task |
| dependent_task_id | UUID | Downstream task |

Constraints:

- Both task IDs reference `task_definitions`.
- Unique `(predecessor_task_id, dependent_task_id)`.
- A task cannot depend on itself.
- Both tasks must belong to the same workflow version.
- Cycles must be rejected during DAG validation.

### 3.5 workflow_executions

Represents one actual run of a workflow.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_version_id | UUID | Exact workflow version |
| status | VARCHAR(30) | Execution status |
| input_data | JSONB | Execution input |
| output_data | JSONB | Final output |
| started_at | TIMESTAMPTZ | Start time |
| completed_at | TIMESTAMPTZ | Completion time |
| created_at | TIMESTAMPTZ | Creation time |
| updated_at | TIMESTAMPTZ | Last update time |

Each execution must reference an immutable workflow version.

### 3.6 task_executions

Represents a task's runtime state within a particular workflow execution.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_execution_id | UUID | Parent execution |
| task_definition_id | UUID | Original task definition |
| status | VARCHAR(30) | Current task status |
| input_data | JSONB | Resolved task input |
| output_data | JSONB | Task result |
| current_attempt | INTEGER | Current attempt number |
| worker_id | VARCHAR(255) | Assigned worker |
| lease_expires_at | TIMESTAMPTZ | Worker lease expiration |
| next_retry_at | TIMESTAMPTZ | Scheduled retry time |
| started_at | TIMESTAMPTZ | Execution start |
| completed_at | TIMESTAMPTZ | Execution completion |
| created_at | TIMESTAMPTZ | Creation time |
| updated_at | TIMESTAMPTZ | Last update time |

Constraints:

- Unique `(workflow_execution_id, task_definition_id)`.
- `current_attempt >= 0`.
- Foreign keys to workflow executions and task definitions.

Important: task execution state must never be stored in `task_definitions`.

### 3.7 task_attempts

Stores every individual attempt to execute a task.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| task_execution_id | UUID | Parent task execution |
| attempt_number | INTEGER | Attempt sequence |
| worker_id | VARCHAR(255) | Worker that executed it |
| status | VARCHAR(30) | Attempt status |
| started_at | TIMESTAMPTZ | Attempt start |
| completed_at | TIMESTAMPTZ | Attempt completion |
| error_message | TEXT | Failure details |
| error_details | JSONB | Structured failure information |
| created_at | TIMESTAMPTZ | Creation time |

Constraints:

- Unique `(task_execution_id, attempt_number)`.
- Attempt numbers must be positive.

Attempts are historical records and must not be overwritten by subsequent retries.

### 3.8 outbox_events

Stores events that must be published to Kafka.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| aggregate_type | VARCHAR(100) | Aggregate type |
| aggregate_id | UUID | Aggregate identifier |
| event_type | VARCHAR(150) | Event type |
| event_payload | JSONB | Serialized event |
| status | VARCHAR(30) | PENDING, PUBLISHED, FAILED |
| created_at | TIMESTAMPTZ | Creation time |
| published_at | TIMESTAMPTZ | Publication time |

The application must insert an outbox record in the same database transaction as the corresponding state change.

An outbox publisher will publish pending records to Kafka and update their status.

Publication can occur more than once. Consumers must therefore support idempotent processing.

### 3.9 execution_events

Stores the execution audit trail.

| Column | Type | Description |
|---|---|---|
| id | UUID | Primary key |
| workflow_execution_id | UUID | Parent execution |
| task_execution_id | UUID | Optional task execution |
| event_type | VARCHAR(150) | Event type |
| event_data | JSONB | Event details |
| occurred_at | TIMESTAMPTZ | Event timestamp |

This table provides a durable history of workflow and task lifecycle transitions.

## 4. State Models

### Workflow execution statuses

- CREATED
- RUNNING
- SUCCEEDED
- FAILED
- CANCELLING
- CANCELLED

### Task execution statuses

- PENDING
- READY
- RUNNING
- RETRY_WAIT
- SUCCEEDED
- FAILED
- SKIPPED
- CANCELLED

### Attempt statuses

- RUNNING
- SUCCEEDED
- FAILED
- TIMED_OUT
- ABANDONED

State transitions must be validated by application services and protected through database transactions.

Terminal task states must not transition back to RUNNING without an explicit recovery or administrative operation.

## 5. Critical Database Indexes

Initial indexes:

| Table | Index |
|---|---|
| workflow_definition_versions | `(workflow_definition_id, version)` UNIQUE |
| task_definitions | `(workflow_version_id, task_key)` UNIQUE |
| task_dependencies | `(predecessor_task_id, dependent_task_id)` UNIQUE |
| workflow_executions | `(status, created_at)` |
| workflow_executions | `(workflow_version_id, created_at)` |
| task_executions | `(status, next_retry_at)` |
| task_executions | `(status, lease_expires_at)` |
| task_executions | `(workflow_execution_id, status)` |
| task_attempts | `(task_execution_id, attempt_number)` UNIQUE |
| outbox_events | `(status, created_at)` |
| execution_events | `(workflow_execution_id, occurred_at)` |

Indexes will be refined using actual query plans and workload measurements.

## 6. Concurrency and Atomic Task Claiming

Multiple scheduler instances may operate simultaneously.

The system must prevent two schedulers from claiming the same task.

The initial approach will use PostgreSQL row locking with `FOR UPDATE SKIP LOCKED`.

Task claiming must happen inside a transaction:

1. Select eligible tasks.
2. Lock selected rows.
3. Change their status to READY or the appropriate claimed state.
4. Persist the corresponding outbox events.
5. Commit the transaction.

Worker assignment must also use an atomic state transition. A worker must not assume that receiving a Kafka event grants permission to execute a task.

## 7. Transactional Boundaries

### Workflow execution creation

One transaction must:

- Create the workflow execution.
- Create all corresponding task execution records.
- Record initial execution history.
- Persist any required initial outbox events.

### Task completion

One transaction must:

- Validate task ownership and execution state.
- Update the task execution.
- Complete the attempt.
- Persist task execution history.
- Persist the task completion outbox event.

### Retry scheduling

One transaction must:

- Complete the failed attempt.
- Update task status to RETRY_WAIT.
- Calculate and persist `next_retry_at`.
- Record the retry decision.

The recovery/scheduling process later makes the task eligible again.

## 8. Worker Leases and Recovery

Workers must periodically renew their task leases.

The scheduler or recovery engine must detect expired leases.

An expired lease does not automatically prove that the previous worker stopped executing. Therefore:

- The old worker must not be allowed to commit results after losing ownership.
- Ownership must be checked during task completion.
- A monotonically increasing fencing token should be considered to protect against stale workers.
- External side effects must use idempotency keys where supported.

## 9. Data Retention and Scalability

Initial implementation:

- Keep execution and attempt history durable.
- Store structured task results as JSONB.
- Avoid storing large binary artifacts directly in PostgreSQL.
- Introduce archival and partitioning only after measuring data growth.
- Establish retention policies before production deployment.

## 10. Migration Strategy

Flyway will manage database schema changes.

Rules:

1. Every schema change must have a versioned migration.
2. Existing migrations must never be edited after deployment.
3. Database migrations must be tested using PostgreSQL Testcontainers.
4. Entity definitions must match the migration schema.
5. Hibernate schema generation must not replace Flyway as the production schema authority.

## 11. Initial Implementation Order

1. Workflow definitions and versioning.
2. Task definitions and dependencies.
3. Workflow executions.
4. Task executions.
5. Task attempts.
6. Execution history.
7. Transactional outbox.
8. Atomic task claiming and worker leases.
9. Retry and recovery support.

## 12. Design Decisions Still Requiring Validation

Before implementation, validate:

- Whether workflow dependencies need conditional branching.
- Whether task execution input should be fully materialized or resolved dynamically.
- Whether task results need a separate artifact storage abstraction.
- Whether outbox publishing requires database polling or CDC at the initial scale.
- Whether lease fencing tokens should be introduced in the first worker implementation.

These decisions must be documented before the relevant implementation phase.