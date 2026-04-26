package com.dwe.worker_service.entity;


import com.dwe.worker_service.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    private String id;

    private String name;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String config;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
}
