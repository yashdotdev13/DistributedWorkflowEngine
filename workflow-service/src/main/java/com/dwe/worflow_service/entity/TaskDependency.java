package com.dwe.worflow_service.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_dependencies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Parent task (must complete first)
    @ManyToOne
    @JoinColumn(name = "parent_task_id", nullable = false)
    private Task parentTask;

    // Child task (depends on parent)
    @ManyToOne
    @JoinColumn(name = "child_task_id", nullable = false)
    private Task childTask;
}