# Distributed Workflow Engine V2 — Execution Lifecycle

**Status:** Proposed

## 1. Purpose

This document defines the lifecycle of workflow executions, task executions, and task attempts.

The goal is to make execution behavior predictable under normal operation, concurrency, retries, and infrastructure failures.

## 2. Workflow Execution Lifecycle

A workflow execution follows these states:

```text
CREATED
   |
   v
RUNNING
   |
   +------> SUCCEEDED
   |
   +------> FAILED
   |
   +------> CANCELLING
                |
                v
             CANCELLED
```

### CREATED

The workflow execution has been accepted and initialized.

### RUNNING

The workflow has started processing eligible tasks.

### SUCCEEDED

All required tasks completed successfully or were legitimately skipped according to the workflow policy.

### FAILED

The workflow reached a terminal failure state.

### CANCELLING

A cancellation request is being processed.

### CANCELLED

The execution has been stopped and remaining work has been prevented from starting.

A workflow execution must not be marked SUCCEEDED while required tasks remain unfinished.

## 3. Task Execution Lifecycle

```text
PENDING
   |
   v
READY
   |
   v
RUNNING
   |
   +------> SUCCEEDED
   |
   +------> FAILED
   |
   +------> RETRY_WAIT
   |            |
   |            v
   |           READY
   |
   +------> CANCELLED

PENDING / READY
   |
   +------> SKIPPED
   |
   +------> CANCELLED
```

### PENDING

The task is waiting for its dependencies.

### READY

All required dependencies have completed successfully and the task is eligible for claiming.

### RUNNING

A worker has successfully claimed the task and owns an active lease.

### RETRY_WAIT

An attempt failed, but another attempt is permitted after a delay.

### SUCCEEDED

The task completed successfully.

### FAILED

The task exhausted its retry policy or encountered a non-retryable failure.

### SKIPPED

The task was intentionally not executed due to workflow policy or dependency outcomes.

### CANCELLED

The task was cancelled before successful completion.

## 4. Dependency Evaluation

For each task, the scheduler evaluates its incoming dependencies.

A task becomes READY only when all required predecessor tasks have reached SUCCEEDED.

If a required predecessor reaches FAILED, the workflow failure policy determines whether the dependent task becomes SKIPPED or the workflow is failed immediately.

Initial implementation policy:

- All dependencies must succeed.
- No conditional branching.
- Failed required dependencies cause downstream tasks to be skipped.
- The workflow becomes FAILED if a required task fails.

Conditional branching and alternative failure policies can be added later.

## 5. Task Claiming

A task must be claimed atomically.

The claim operation must validate:

- Task status is READY.
- Workflow execution permits new task execution.
- The task has no active owner.
- The task is eligible under its retry schedule.
- The task has not exceeded its execution policy.

On successful claim:

1. Transition task to RUNNING.
2. Assign worker identifier.
3. Increment the fencing token.
4. Set lease expiration.
5. Create a new attempt record.
6. Record task-start history.
7. Commit the transaction.

Only after the transaction commits may the worker execute the handler.

A worker that fails to claim a task must not execute it.

## 6. Worker Lease and Heartbeat

Every running task has a lease.

The worker periodically renews the lease while executing.

A heartbeat operation must verify:

- Task is still RUNNING.
- Worker identifier matches the current owner.
- Fencing token matches the current ownership generation.

If the worker cannot renew its lease, it must treat its ownership as lost.

A worker must not commit task results after losing ownership.

## 7. Fencing Tokens

Each successful claim receives a monotonically increasing fencing token for that task execution.

Example:

| Claim | Fencing token |
|---|---:|
| First worker | 1 |
| Recovered claim | 2 |
| Next recovered claim | 3 |

Every completion or heartbeat operation must verify the current token.

This prevents stale workers from overwriting results after a task has been reclaimed.

Fencing tokens protect engine state. External systems must independently support idempotency or equivalent safeguards.

## 8. Retry Policy

Each task definition supports:

- Maximum retry count.
- Retry delay.
- Backoff multiplier.
- Maximum backoff duration.
- Retryable error classification.

Initial retry algorithm:

`delay = min(initialDelay * multiplier^(attemptNumber - 1), maximumDelay)`

Optional jitter should be introduced to prevent synchronized retry bursts.

When a task attempt fails:

1. Persist the failed attempt.
2. Classify the error.
3. Check retry eligibility.
4. If retryable and attempts remain, set RETRY_WAIT.
5. Persist `next_retry_at`.
6. Publish TaskRetryScheduledEvent through the outbox.
7. Scheduler makes the task READY when its retry time arrives.

Non-retryable errors or exhausted retries transition the task to FAILED.

## 9. Timeout Handling

Task definitions specify an execution timeout.

The execution engine tracks:

- Task start time.
- Attempt start time.
- Lease expiration.
- Configured execution timeout.

Timeout processing must distinguish:

- A worker that stopped renewing its lease.
- A task that exceeded its execution timeout.
- A task whose result arrived after ownership expired.

A timed-out attempt must be recorded as TIMED_OUT or ABANDONED as appropriate.

The task may be retried only after the previous ownership has been invalidated.

## 10. Worker Crash Recovery

The recovery engine periodically scans for expired RUNNING tasks.

Recovery procedure:

1. Find expired leases.
2. Lock affected task execution rows.
3. Verify the lease is still expired.
4. Invalidate previous ownership by incrementing the fencing token.
5. Mark the previous attempt ABANDONED when appropriate.
6. Evaluate retry policy.
7. Move task to RETRY_WAIT or FAILED.
8. Record recovery history.
9. Persist resulting events through the outbox.
10. Commit the transaction.

Recovery operations must be safe to execute concurrently.

## 11. Workflow Completion

A workflow is SUCCEEDED when:

- Every required task is SUCCEEDED or legitimately SKIPPED.
- No task remains PENDING, READY, RUNNING, or RETRY_WAIT.
- No required task has FAILED.

A workflow is FAILED when a required task reaches terminal failure under the selected failure policy.

The final state must be computed from durable task execution state, not from assumptions based only on Kafka events.

## 12. Cancellation

When a workflow cancellation is requested:

1. Transition the workflow to CANCELLING.
2. Prevent new task claims.
3. Cancel tasks that have not started.
4. Signal running workers where cooperative cancellation is supported.
5. Invalidate task ownership as appropriate.
6. Record cancellation history.
7. Transition the workflow to CANCELLED once cancellation is finalized.

Cancellation cannot guarantee rollback of external side effects already performed.

## 13. Concurrency Rules

- Multiple independent tasks may run concurrently.
- A task execution may have only one valid current owner.
- A task definition can participate in multiple workflow executions.
- Each workflow execution has independent task execution records.
- Scheduler and recovery operations must use database-level concurrency control.
- Kafka event delivery must not be treated as proof of exclusive ownership.

## 14. Execution Invariants

The following invariants must always hold:

1. A task execution belongs to exactly one workflow execution.
2. A workflow execution references one immutable workflow version.
3. A task execution has at most one current valid owner.
4. Every retry creates a new task attempt.
5. A terminal task execution cannot be silently restarted.
6. A dependent task cannot execute before all required dependencies succeed.
7. A stale worker cannot overwrite a newer attempt's result.
8. Workflow success requires all required tasks to be complete.
9. Duplicate events must not create unintended duplicate attempts.
10. Execution history must preserve significant lifecycle transitions.

## 15. Initial Acceptance Scenarios

### Scenario A: Simple dependency graph

Given:

`A -> B -> C`

Expected:

- A executes first.
- B executes only after A succeeds.
- C executes only after B succeeds.
- Workflow completes successfully.

### Scenario B: Parallel tasks

Given:

```text
       A
      / \
     B   C
      \ /
       D
```

Expected:

- A executes first.
- B and C become eligible independently.
- D waits for both B and C.
- B and C may execute concurrently.

### Scenario C: Task failure and retry

Given a task with two permitted retries:

- First attempt fails.
- Task enters RETRY_WAIT.
- Task becomes eligible after its retry delay.
- New attempt is created.
- Final task state reflects the eventual outcome.

### Scenario D: Worker crash

- Worker claims a task.
- Worker stops renewing its lease.
- Recovery detects expiration.
- Previous ownership is invalidated.
- A new attempt is scheduled if allowed.
- Stale worker completion is rejected.

### Scenario E: Duplicate event

- Kafka delivers the same TaskReadyEvent more than once.
- Inbox deduplication and atomic claiming prevent unintended duplicate execution.

### Scenario F: Multiple workflow executions

- The same workflow definition is started twice.
- Both executions maintain independent task state.
- Their tasks can execute concurrently without cross-execution interference.