package com.dwe.events;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskReadyEvent {

    private String taskId;
    private String workflowId;
    private String taskName;
    private String taskType;
    private String config;
}