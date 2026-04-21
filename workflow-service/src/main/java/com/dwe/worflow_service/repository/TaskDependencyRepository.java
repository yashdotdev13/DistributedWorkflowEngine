package com.dwe.worflow_service.repository;




import com.dwe.worflow_service.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    List<TaskDependency> findByParentTaskId(String parentTaskId);

    List<TaskDependency> findByChildTaskId(String childTaskId);
}