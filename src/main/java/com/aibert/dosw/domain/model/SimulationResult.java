package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulationResult {

    private Double targetGrade;
    private Double requiredGrade;
    private boolean achievable;
    private Double pendingPercentage;
}
