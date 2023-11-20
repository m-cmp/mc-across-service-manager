package com.mcmp.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringResultDTO {
    private String agentStatus;
    private char agentDeployYN;
    private String agentDeployDate;
}
