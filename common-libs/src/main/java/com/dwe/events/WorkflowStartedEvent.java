package com.dwe.events;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStartedEvent extends BaseEvent{

    private String workflowId;
    private String workflowName;
}
