package com.dwe.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
class FlywayMigrationIntegrationTest {

  @Container
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

  private String schemaName;

  @BeforeEach
  void createIsolatedSchema() throws SQLException {
    schemaName = "test_" + UUID.randomUUID().toString().replace("-", "");

    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA \"" + schemaName + "\"");
    }
  }

  @AfterEach
  void dropIsolatedSchema() throws SQLException {
    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("DROP SCHEMA IF EXISTS \"" + schemaName + "\" CASCADE");
    }
  }

  @Test
  void shouldApplyInitialSchemaSuccessfully() throws Exception {
    Flyway flyway = createFlyway();

    var result = flyway.migrate();

    assertEquals(1, result.migrationsExecuted);

    try (Connection connection = getConnection();
        Statement statement = connection.createStatement();
        var resultSet =
            statement.executeQuery(
                """
                         SELECT COUNT(*)
                         FROM information_schema.tables
                         WHERE table_schema = '%s'
                         AND table_type = 'BASE TABLE'
                         AND table_name IN (
                             'workflow_definitions',
                             'workflow_definition_versions',
                             'task_definitions',
                             'task_dependencies',
                             'workflow_executions',
                             'task_executions',
                             'task_attempts',
                             'outbox_events',
                             'execution_events'
                         )
                         """
                    .formatted(schemaName))) {

      resultSet.next();
      assertEquals(9, resultSet.getInt(1));
    }
  }

  @Test
  void shouldNotReapplyAlreadyExecutedMigration() {
    Flyway flyway = createFlyway();

    var firstMigration = flyway.migrate();
    var secondMigration = flyway.migrate();

    assertEquals(1, firstMigration.migrationsExecuted);
    assertEquals(0, secondMigration.migrationsExecuted);
  }

  @Test
  void shouldRejectNegativeTaskRetryCount() throws Exception {
    createFlyway().migrate();

    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {

      statement.execute(
          """
              INSERT INTO workflow_definitions (id, name)
              VALUES (
                  '11111111-1111-1111-1111-111111111111',
                  'Test Workflow'
              )
              """);

      statement.execute(
          """
              INSERT INTO workflow_definition_versions (
                  id,
                  workflow_definition_id,
                  version,
                  status
              )
              VALUES (
                  '22222222-2222-2222-2222-222222222222',
                  '11111111-1111-1111-1111-111111111111',
                  1,
                  'DRAFT'
              )
              """);

      SQLException exception =
          assertThrows(
              SQLException.class,
              () ->
                  statement.execute(
                      """
                                      INSERT INTO task_definitions (
                                          id,
                                          workflow_version_id,
                                          task_key,
                                          task_type,
                                          max_retries,
                                          timeout_seconds
                                      )
                                      VALUES (
                                          '33333333-3333-3333-3333-333333333333',
                                          '22222222-2222-2222-2222-222222222222',
                                          'invalid-task',
                                          'TEST',
                                          -1,
                                          30
                                      )
                                      """));

      assertEquals("23514", exception.getSQLState());
    }
  }

  private Flyway createFlyway() {
    return Flyway.configure()
        .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
        .schemas(schemaName)
        .defaultSchema(schemaName)
        .locations("classpath:db/migration")
        .load();
  }

  private Connection getConnection() throws SQLException {
    Connection connection =
        DriverManager.getConnection(
            POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());

    try (Statement statement = connection.createStatement()) {
      statement.execute("SET search_path TO \"" + schemaName + "\"");
    }

    return connection;
  }
}
