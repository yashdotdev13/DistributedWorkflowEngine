
-- ============================================================
-- Distributed Workflow Engine V2
-- V1: Initial core workflow schema
-- Database: PostgreSQL
-- ============================================================

-- 1. Workflow definitions

CREATE TABLE workflow_definitions (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
                                      description TEXT,
                                      created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Immutable workflow definition versions

CREATE TABLE workflow_definition_versions (
                                              id UUID PRIMARY KEY,
                                              workflow_definition_id UUID NOT NULL,
                                              version INTEGER NOT NULL,
                                              status VARCHAR(30) NOT NULL,
                                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              published_at TIMESTAMPTZ,

                                              CONSTRAINT fk_workflow_version_definition
                                                  FOREIGN KEY (workflow_definition_id)
                                                      REFERENCES workflow_definitions(id),

                                              CONSTRAINT uk_workflow_definition_version
                                                  UNIQUE (workflow_definition_id, version),

                                              CONSTRAINT chk_workflow_version_positive
                                                  CHECK (version > 0),

                                              CONSTRAINT chk_workflow_version_status
                                                  CHECK (status IN ('DRAFT', 'PUBLISHED'))
);

-- 3. Task definitions

CREATE TABLE task_definitions (
                                  id UUID PRIMARY KEY,
                                  workflow_version_id UUID NOT NULL,
                                  task_key VARCHAR(255) NOT NULL,
                                  task_type VARCHAR(100) NOT NULL,
                                  configuration JSONB NOT NULL DEFAULT '{}'::jsonb,
                                  max_retries INTEGER NOT NULL DEFAULT 0,
                                  timeout_seconds INTEGER NOT NULL,
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_task_definition_version
                                      FOREIGN KEY (workflow_version_id)
                                          REFERENCES workflow_definition_versions(id),

                                  CONSTRAINT uk_task_definition_key
                                      UNIQUE (workflow_version_id, task_key),

                                  CONSTRAINT chk_task_max_retries
                                      CHECK (max_retries >= 0),

                                  CONSTRAINT chk_task_timeout
                                      CHECK (timeout_seconds > 0),

                                  CONSTRAINT uk_task_definition_id_version
                                      UNIQUE (id, workflow_version_id)
);

-- 4. Task dependencies

-- 4. Task dependencies

CREATE TABLE task_dependencies (
                                   id UUID PRIMARY KEY,
                                   workflow_version_id UUID NOT NULL,
                                   predecessor_task_id UUID NOT NULL,
                                   successor_task_id UUID NOT NULL,
                                   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_dependency_workflow_version
                                       FOREIGN KEY (workflow_version_id)
                                           REFERENCES workflow_definition_versions(id),

                                   CONSTRAINT fk_dependency_predecessor
                                       FOREIGN KEY (predecessor_task_id, workflow_version_id)
                                           REFERENCES task_definitions(id, workflow_version_id),

                                   CONSTRAINT fk_dependency_successor
                                       FOREIGN KEY (successor_task_id, workflow_version_id)
                                           REFERENCES task_definitions(id, workflow_version_id),

                                   CONSTRAINT uk_task_dependency
                                       UNIQUE (predecessor_task_id, successor_task_id),

                                   CONSTRAINT chk_task_not_self_dependent
                                       CHECK (predecessor_task_id <> successor_task_id)
);

-- 5. Workflow executions

CREATE TABLE workflow_executions (
                                     id UUID PRIMARY KEY,
                                     workflow_version_id UUID NOT NULL,
                                     status VARCHAR(30) NOT NULL,
                                     input_data JSONB,
                                     output_data JSONB,
                                     started_at TIMESTAMPTZ,
                                     completed_at TIMESTAMPTZ,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_execution_workflow_version
                                         FOREIGN KEY (workflow_version_id)
                                             REFERENCES workflow_definition_versions(id),

                                     CONSTRAINT chk_workflow_execution_status
                                         CHECK (status IN (
                                                           'CREATED',
                                                           'RUNNING',
                                                           'SUCCEEDED',
                                                           'FAILED',
                                                           'CANCELLING',
                                                           'CANCELLED'
                                             ))
);

-- 6. Task executions

CREATE TABLE task_executions (
                                 id UUID PRIMARY KEY,
                                 workflow_execution_id UUID NOT NULL,
                                 task_definition_id UUID NOT NULL,
                                 status VARCHAR(30) NOT NULL,
                                 input_data JSONB,
                                 output_data JSONB,
                                 current_attempt INTEGER NOT NULL DEFAULT 0,
                                 worker_id VARCHAR(255),
                                 lease_expires_at TIMESTAMPTZ,
                                 next_retry_at TIMESTAMPTZ,
                                 started_at TIMESTAMPTZ,
                                 completed_at TIMESTAMPTZ,
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_task_execution_workflow
                                     FOREIGN KEY (workflow_execution_id)
                                         REFERENCES workflow_executions(id),

                                 CONSTRAINT fk_task_execution_definition
                                     FOREIGN KEY (task_definition_id)
                                         REFERENCES task_definitions(id),

                                 CONSTRAINT uk_workflow_task_execution
                                     UNIQUE (workflow_execution_id, task_definition_id),

                                 CONSTRAINT chk_task_current_attempt
                                     CHECK (current_attempt >= 0),

                                 CONSTRAINT chk_task_execution_status
                                     CHECK (status IN (
                                                       'PENDING',
                                                       'READY',
                                                       'RUNNING',
                                                       'RETRY_WAIT',
                                                       'SUCCEEDED',
                                                       'FAILED',
                                                       'SKIPPED',
                                                       'CANCELLED'
                                         ))
);

-- 7. Task attempts

CREATE TABLE task_attempts (
                               id UUID PRIMARY KEY,
                               task_execution_id UUID NOT NULL,
                               attempt_number INTEGER NOT NULL,
                               worker_id VARCHAR(255),
                               status VARCHAR(30) NOT NULL,
                               started_at TIMESTAMPTZ,
                               completed_at TIMESTAMPTZ,
                               error_message TEXT,
                               error_details JSONB,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_task_attempt_execution
                                   FOREIGN KEY (task_execution_id)
                                       REFERENCES task_executions(id),

                               CONSTRAINT uk_task_attempt_number
                                   UNIQUE (task_execution_id, attempt_number),

                               CONSTRAINT chk_task_attempt_positive
                                   CHECK (attempt_number > 0),

                               CONSTRAINT chk_task_attempt_status
                                   CHECK (status IN (
                                                     'RUNNING',
                                                     'SUCCEEDED',
                                                     'FAILED',
                                                     'TIMED_OUT',
                                                     'ABANDONED'
                                       ))
);

-- 8. Transactional outbox

CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(100) NOT NULL,
                               aggregate_id UUID NOT NULL,
                               event_type VARCHAR(150) NOT NULL,
                               event_payload JSONB NOT NULL,
                               status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               published_at TIMESTAMPTZ,

                               CONSTRAINT chk_outbox_event_status
                                   CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

-- 9. Execution history / audit events

CREATE TABLE execution_events (
                                  id UUID PRIMARY KEY,
                                  workflow_execution_id UUID NOT NULL,
                                  task_execution_id UUID,
                                  event_type VARCHAR(150) NOT NULL,
                                  event_data JSONB,
                                  occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_execution_event_workflow
                                      FOREIGN KEY (workflow_execution_id)
                                          REFERENCES workflow_executions(id),

                                  CONSTRAINT fk_execution_event_task
                                      FOREIGN KEY (task_execution_id)
                                          REFERENCES task_executions(id)
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX idx_workflow_executions_status_created
    ON workflow_executions(status, created_at);

CREATE INDEX idx_workflow_executions_version_created
    ON workflow_executions(workflow_version_id, created_at);

CREATE INDEX idx_task_executions_status_retry
    ON task_executions(status, next_retry_at);

CREATE INDEX idx_task_executions_status_lease
    ON task_executions(status, lease_expires_at);

CREATE INDEX idx_task_executions_workflow_status
    ON task_executions(workflow_execution_id, status);

CREATE INDEX idx_outbox_events_status_created
    ON outbox_events(status, created_at);

CREATE INDEX idx_execution_events_workflow_occurred
    ON execution_events(workflow_execution_id, occurred_at);

CREATE INDEX idx_task_dependencies_workflow_version
    ON task_dependencies(workflow_version_id);

CREATE INDEX idx_task_dependencies_predecessor
    ON task_dependencies(predecessor_task_id);

CREATE INDEX idx_task_dependencies_successor
    ON task_dependencies(successor_task_id);
