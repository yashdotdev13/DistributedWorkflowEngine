package com.dwe.worflow_service.entity;


import com.dwe.worflow_service.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // API_CALL, DB_WRITE, SCRIPT

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String config;

    private Integer retryCount;
    private Integer maxRetries;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🔗 Relation to workflow
    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonBackReference
    private Workflow workflow;
}
