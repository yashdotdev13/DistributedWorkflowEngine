package com.dwe.events;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCompletedEvent {


    private String taskId;
    private String workflowId;
    private String status;
    private String result;
}
