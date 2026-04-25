package com.dwe.scheduler_service.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workflow {

    @Id
    private String id;

    private String name;
}
