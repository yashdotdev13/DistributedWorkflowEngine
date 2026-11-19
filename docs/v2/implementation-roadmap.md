# Distributed Workflow Engine V2 — Implementation Roadmap

## Phase 0 — Legacy Preservation

**Status:** Completed

- [x] Preserve original implementation.
- [x] Create `legacy-v1-baseline` tag.
- [x] Create `v2-development` branch.
- [x] Update canonical Git remote.

## Phase 1 — Architecture and Engineering Foundation

**Status:** In progress

- [x] Define system architecture.
- [x] Design database model.
- [x] Define event-driven communication.
- [x] Define execution lifecycle.
- [ ] Complete architecture consistency review.
- [ ] Finalize implementation roadmap.
- [ ] Commit architecture documentation.

## Phase 2 — Project Foundation

- [ ] Establish Maven multi-module structure.
- [ ] Configure Java 21 and Spring Boot.
- [ ] Establish dependency and version management.
- [ ] Configure code formatting and static analysis.
- [ ] Configure JUnit 5 and Testcontainers.
- [ ] Configure Flyway and PostgreSQL.
- [ ] Establish CI pipeline.
- [ ] Add Docker Compose development environment.

**Acceptance criteria:** Clean build, working CI, and database migrations running successfully.

## Phase 3 — Workflow Definition and DAG Engine

- [ ] Implement workflow definitions.
- [ ] Implement workflow versioning.
- [ ] Implement task definitions.
- [ ] Implement dependency management.
- [ ] Implement DAG validation.
- [ ] Detect cycles.
- [ ] Add unit and integration tests.

**Acceptance criteria:** Valid DAGs are accepted, invalid DAGs are rejected, and published versions are immutable.

## Phase 4 — Durable Execution Engine

- [ ] Implement workflow executions.
- [ ] Implement task executions.
- [ ] Implement task attempts.
- [ ] Implement workflow and task state machines.
- [ ] Implement execution history.
- [ ] Implement workflow creation transaction.
- [ ] Implement workflow and task status APIs.

**Acceptance criteria:** Multiple executions of one workflow maintain independent state.

## Phase 5 — Event Infrastructure

- [ ] Define versioned event contracts.
- [ ] Configure Kafka topics.
- [ ] Implement transactional outbox.
- [ ] Implement outbox publisher.
- [ ] Implement consumer inbox.
- [ ] Implement idempotent consumers.
- [ ] Add serialization and contract tests.

**Acceptance criteria:** Database state changes and corresponding outgoing events remain consistent, including during failures.

## Phase 6 — Scheduler

- [ ] Implement dependency evaluation.
- [ ] Implement atomic task readiness transitions.
- [ ] Implement task dispatch.
- [ ] Implement concurrent scheduler coordination.
- [ ] Add scheduler integration tests.

**Acceptance criteria:** Independent tasks become READY concurrently, and dependent tasks wait for successful prerequisites.

## Phase 7 — Worker Runtime

- [ ] Define TaskHandler interface.
- [ ] Implement TaskContext.
- [ ] Implement built-in task handlers.
- [ ] Implement atomic task claiming.
- [ ] Implement worker leases.
- [ ] Implement heartbeat renewal.
- [ ] Implement task result persistence.
- [ ] Add worker integration tests.

**Acceptance criteria:** Multiple workers can execute tasks without duplicate ownership.

## Phase 8 — Retry and Recovery

- [ ] Implement retry policies.
- [ ] Implement exponential backoff and jitter.
- [ ] Implement execution timeouts.
- [ ] Implement expired lease detection.
- [ ] Implement fencing tokens.
- [ ] Implement worker crash recovery.
- [ ] Implement dead-letter handling.

**Acceptance criteria:** Failed and abandoned tasks recover safely without allowing stale workers to overwrite newer results.

## Phase 9 — Workflow Lifecycle and Advanced Capabilities

- [ ] Implement workflow cancellation.
- [ ] Implement failure propagation.
- [ ] Implement execution output aggregation.
- [ ] Implement configurable concurrency limits.
- [ ] Evaluate conditional branching.
- [ ] Evaluate workflow pause and resume.

**Acceptance criteria:** Workflow lifecycle operations remain consistent under concurrent execution.

## Phase 10 — Observability and Production Readiness

- [ ] Add structured logging.
- [ ] Add metrics with Micrometer.
- [ ] Integrate Prometheus and Grafana.
- [ ] Add OpenTelemetry tracing.
- [ ] Add operational health checks.
- [ ] Add performance and load tests.
- [ ] Document deployment and operations.
- [ ] Document failure recovery procedures.

**Acceptance criteria:** Operators can inspect execution history, diagnose failures, monitor workers, and measure scheduling and execution performance.

## Engineering Rules

1. Every feature must include appropriate tests.
2. Every database schema change must use Flyway.
3. No direct database/Kafka dual writes.
4. No exactly-once execution claims.
5. No task execution without successful atomic ownership.
6. No destructive modifications to the legacy implementation.
7. Each implementation phase should be delivered through focused feature branches and pull requests.