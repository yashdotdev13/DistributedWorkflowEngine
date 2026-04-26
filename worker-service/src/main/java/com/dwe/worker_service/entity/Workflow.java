package com.dwe.worker_service.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {

    @Id
    private String id;

    private String name;
}