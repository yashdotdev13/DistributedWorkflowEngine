package com.dwe.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dwe.common.status.WorkflowVersionStatus;
import com.dwe.persistence.config.DwePersistenceAutoConfiguration;
import com.dwe.persistence.entity.TaskDefinitionEntity;
import com.dwe.persistence.entity.TaskDependencyEntity;
import com.dwe.persistence.entity.WorkflowDefinitionEntity;
import com.dwe.persistence.entity.WorkflowDefinitionVersionEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(
    classes = WorkflowDefinitionRepositoryIntegrationTest.JpaTestConfiguration.class)
@Import(DwePersistenceAutoConfiguration.class)
class WorkflowDefinitionRepositoryIntegrationTest {

  @Container
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

  @Autowired private WorkflowDefinitionRepository repository;

  @Autowired private WorkflowDefinitionVersionRepository versionRepository;

  @Autowired private TaskDefinitionRepository taskDefinitionRepository;

  @Autowired private TaskDependencyRepository taskDependencyRepository;

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.flyway.enabled", () -> "true");
  }

  @Test
  void shouldSaveAndRetrieveWorkflowDefinition() {
    WorkflowDefinitionEntity entity =
        new WorkflowDefinitionEntity("Order Processing", "Processes customer orders");

    WorkflowDefinitionEntity saved = repository.saveAndFlush(entity);

    assertThat(saved.getId()).isNotNull();

    var retrieved = repository.findById(saved.getId());

    assertThat(retrieved).isPresent();
    assertThat(retrieved.get().getName()).isEqualTo("Order Processing");
    assertThat(retrieved.get().getDescription()).isEqualTo("Processes customer orders");
  }

  @Test
  void shouldGenerateUniqueUuidForWorkflowDefinitions() {
    WorkflowDefinitionEntity first =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Workflow One", null));

    WorkflowDefinitionEntity second =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Workflow Two", null));

    assertThat(first.getId()).isNotNull();
    assertThat(second.getId()).isNotNull();
    assertThat(first.getId()).isNotEqualTo(second.getId());
  }

  @Test
  void shouldPopulateCreationAndUpdateTimestamps() {
    WorkflowDefinitionEntity entity =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Timestamp Test", null));

    assertThat(entity.getCreatedAt()).isNotNull();
    assertThat(entity.getUpdatedAt()).isNotNull();
    assertThat(entity.getUpdatedAt()).isAfterOrEqualTo(entity.getCreatedAt());
  }

  @Test
  void shouldUpdateWorkflowDefinitionMetadata() {
    WorkflowDefinitionEntity entity =
        repository.saveAndFlush(
            new WorkflowDefinitionEntity("Original Name", "Original description"));

    Instant originalCreatedAt = entity.getCreatedAt();

    entity.updateDetails("Updated Name", "Updated description");

    WorkflowDefinitionEntity updated = repository.saveAndFlush(entity);

    assertThat(updated.getName()).isEqualTo("Updated Name");
    assertThat(updated.getDescription()).isEqualTo("Updated description");
    assertThat(updated.getCreatedAt()).isEqualTo(originalCreatedAt);
    assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalCreatedAt);
  }

  @Test
  void shouldDeleteWorkflowDefinition() {
    WorkflowDefinitionEntity entity =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Delete Test", null));

    UUID id = entity.getId();

    repository.delete(entity);
    repository.flush();

    assertThat(repository.findById(id)).isEmpty();
  }

  @Test
  void shouldCheckWhetherWorkflowNameExists() {
    repository.saveAndFlush(new WorkflowDefinitionEntity("Existing Workflow", null));

    assertThat(repository.existsByName("Existing Workflow")).isTrue();
    assertThat(repository.existsByName("Missing Workflow")).isFalse();
  }

  @Test
  void shouldCreateWorkflowDefinitionVersion() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Order Processing", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    assertThat(version.getId()).isNotNull();
    assertThat(version.getVersion()).isEqualTo(1);
    assertThat(version.getStatus()).isEqualTo(WorkflowVersionStatus.DRAFT);
    assertThat(version.getCreatedAt()).isNotNull();
    assertThat(version.getWorkflowDefinition().getId()).isEqualTo(workflow.getId());
  }

  @Test
  void shouldFindVersionByWorkflowIdAndVersionNumber() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Payment Workflow", null));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    var result = versionRepository.findByWorkflowDefinition_IdAndVersion(workflow.getId(), 1);

    assertThat(result).isPresent();
    assertThat(result.get().getVersion()).isEqualTo(1);
  }

  @Test
  void shouldCheckWhetherWorkflowVersionExists() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Inventory Workflow", null));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    assertThat(versionRepository.existsByWorkflowDefinition_IdAndVersion(workflow.getId(), 1))
        .isTrue();

    assertThat(versionRepository.existsByWorkflowDefinition_IdAndVersion(workflow.getId(), 2))
        .isFalse();
  }

  @Test
  void shouldRetrieveWorkflowVersionsInDescendingOrder() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Reporting Workflow", null));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 2));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 3));

    var versions =
        versionRepository.findAllByWorkflowDefinition_IdOrderByVersionDesc(workflow.getId());

    assertThat(versions)
        .extracting(WorkflowDefinitionVersionEntity::getVersion)
        .containsExactly(3, 2, 1);
  }

  @Test
  void shouldRetrieveLatestWorkflowVersion() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Latest Version Workflow", null));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 2));

    var latest =
        versionRepository.findFirstByWorkflowDefinition_IdOrderByVersionDesc(workflow.getId());

    assertThat(latest).isPresent();
    assertThat(latest.get().getVersion()).isEqualTo(2);
  }

  @Test
  void shouldPublishDraftWorkflowVersion() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Publishing Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    version.publish();

    WorkflowDefinitionVersionEntity published = versionRepository.saveAndFlush(version);

    assertThat(published.getStatus()).isEqualTo(WorkflowVersionStatus.PUBLISHED);

    assertThat(published.getPublishedAt()).isNotNull();
  }

  @Test
  void shouldCreateTaskDefinition() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Order Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity task =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(
                version, "validate-order", "VALIDATION", "{\"strict\":true}", 3, 60));

    assertThat(task.getId()).isNotNull();
    assertThat(task.getTaskKey()).isEqualTo("validate-order");
    assertThat(task.getTaskType()).isEqualTo("VALIDATION");
    assertThat(task.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldPersistAndRetrieveJsonbConfiguration() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Payment Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity task =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(
                version,
                "process-payment",
                "PAYMENT",
                "{\"currency\":\"USD\",\"amount\":100}",
                2,
                120));

    var retrieved = taskDefinitionRepository.findById(task.getId());

    assertThat(retrieved).isPresent();
    assertThat(retrieved.get().getConfiguration())
        .isEqualTo("{\"currency\":\"USD\",\"amount\":100}");
  }

  @Test
  void shouldFindTaskByWorkflowVersionAndTaskKey() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Inventory Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    taskDefinitionRepository.saveAndFlush(
        new TaskDefinitionEntity(version, "reserve-stock", "INVENTORY", "{}", 2, 60));

    var result =
        taskDefinitionRepository.findByWorkflowVersion_IdAndTaskKey(
            version.getId(), "reserve-stock");

    assertThat(result).isPresent();
    assertThat(result.get().getTaskKey()).isEqualTo("reserve-stock");
  }

  @Test
  void shouldAllowSameTaskKeyInDifferentWorkflowVersions() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Versioned Workflow", null));

    WorkflowDefinitionVersionEntity firstVersion =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    WorkflowDefinitionVersionEntity secondVersion =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 2));

    TaskDefinitionEntity firstTask =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(firstVersion, "process-order", "ORDER", "{}", 1, 60));

    TaskDefinitionEntity secondTask =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(secondVersion, "process-order", "ORDER", "{}", 2, 90));

    assertThat(firstTask.getId()).isNotEqualTo(secondTask.getId());
    assertThat(
            taskDefinitionRepository.existsByWorkflowVersion_IdAndTaskKey(
                firstVersion.getId(), "process-order"))
        .isTrue();
    assertThat(
            taskDefinitionRepository.existsByWorkflowVersion_IdAndTaskKey(
                secondVersion.getId(), "process-order"))
        .isTrue();
  }

  @Test
  void shouldRetrieveTasksInTaskKeyOrder() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Task Ordering Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    taskDefinitionRepository.saveAndFlush(
        new TaskDefinitionEntity(version, "validate", "VALIDATION", "{}", 1, 30));

    taskDefinitionRepository.saveAndFlush(
        new TaskDefinitionEntity(version, "process", "PROCESSING", "{}", 2, 60));

    taskDefinitionRepository.saveAndFlush(
        new TaskDefinitionEntity(version, "authorize", "AUTHORIZATION", "{}", 0, 30));

    var tasks =
        taskDefinitionRepository.findAllByWorkflowVersion_IdOrderByTaskKeyAsc(version.getId());

    assertThat(tasks)
        .extracting(TaskDefinitionEntity::getTaskKey)
        .containsExactly("authorize", "process", "validate");

    assertThat(taskDefinitionRepository.countByWorkflowVersion_Id(version.getId())).isEqualTo(3);
  }

  @Test
  void shouldRejectNegativeRetryCount() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Invalid Retry Workflow", null));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    assertThat(
            org.junit.jupiter.api.Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () ->
                    taskDefinitionRepository.saveAndFlush(
                        new TaskDefinitionEntity(version, "invalid-task", "TEST", "{}", -1, 30))))
        .isNotNull();
  }

  @Test
  void shouldCreateTaskDependency() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(
            new WorkflowDefinitionEntity("Dependency Workflow", "Test workflow"));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity predecessor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-a", "HTTP", "{}", 3, 60));

    TaskDefinitionEntity successor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-b", "HTTP", "{}", 3, 60));

    TaskDependencyEntity dependency =
        taskDependencyRepository.saveAndFlush(
            new TaskDependencyEntity(version, predecessor, successor));

    assertThat(dependency.getId()).isNotNull();
    assertThat(dependency.getCreatedAt()).isNotNull();
    assertThat(taskDependencyRepository.countByWorkflowVersion_Id(version.getId())).isEqualTo(1);
  }

  @Test
  void shouldFindDependenciesByPredecessorAndSuccessor() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Lookup Workflow", "Test workflow"));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity predecessor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-a", "HTTP", "{}", 3, 60));

    TaskDefinitionEntity successor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-b", "HTTP", "{}", 3, 60));

    taskDependencyRepository.saveAndFlush(
        new TaskDependencyEntity(version, predecessor, successor));

    assertThat(taskDependencyRepository.findAllByPredecessorTask_Id(predecessor.getId()))
        .hasSize(1);

    assertThat(taskDependencyRepository.findAllBySuccessorTask_Id(successor.getId())).hasSize(1);

    assertThat(
            taskDependencyRepository.existsByPredecessorTask_IdAndSuccessorTask_Id(
                predecessor.getId(), successor.getId()))
        .isTrue();
  }

  @Test
  void shouldRejectDuplicateTaskDependency() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(
            new WorkflowDefinitionEntity("Duplicate Workflow", "Test workflow"));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity predecessor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-a", "HTTP", "{}", 3, 60));

    TaskDefinitionEntity successor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-b", "HTTP", "{}", 3, 60));

    taskDependencyRepository.saveAndFlush(
        new TaskDependencyEntity(version, predecessor, successor));

    assertThatThrownBy(
            () ->
                taskDependencyRepository.saveAndFlush(
                    new TaskDependencyEntity(version, predecessor, successor)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void shouldRejectSelfDependency() {
    WorkflowDefinitionEntity workflow =
        repository.saveAndFlush(
            new WorkflowDefinitionEntity("Self Dependency Workflow", "Test workflow"));

    WorkflowDefinitionVersionEntity version =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(workflow, 1));

    TaskDefinitionEntity task =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(version, "task-a", "HTTP", "{}", 3, 60));

    assertThatThrownBy(
            () ->
                taskDependencyRepository.saveAndFlush(
                    new TaskDependencyEntity(version, task, task)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void shouldRejectDependencyAcrossWorkflowVersions() {
    WorkflowDefinitionEntity firstWorkflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("First Workflow", "First workflow"));

    WorkflowDefinitionVersionEntity firstVersion =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(firstWorkflow, 1));

    WorkflowDefinitionEntity secondWorkflow =
        repository.saveAndFlush(new WorkflowDefinitionEntity("Second Workflow", "Second workflow"));

    WorkflowDefinitionVersionEntity secondVersion =
        versionRepository.saveAndFlush(new WorkflowDefinitionVersionEntity(secondWorkflow, 1));

    TaskDefinitionEntity predecessor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(firstVersion, "task-a", "HTTP", "{}", 3, 60));

    TaskDefinitionEntity successor =
        taskDefinitionRepository.saveAndFlush(
            new TaskDefinitionEntity(secondVersion, "task-b", "HTTP", "{}", 3, 60));

    assertThatThrownBy(
            () ->
                taskDependencyRepository.saveAndFlush(
                    new TaskDependencyEntity(firstVersion, predecessor, successor)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Configuration(proxyBeanMethods = false)
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = WorkflowDefinitionEntity.class)
  @EnableJpaRepositories(basePackageClasses = WorkflowDefinitionRepository.class)
  static class JpaTestConfiguration {}
}
