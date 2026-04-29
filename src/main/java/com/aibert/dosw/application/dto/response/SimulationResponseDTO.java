package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponseDTO {

    private Double targetGrade;
    private Double requiredGrade;
    private boolean achievable;
    private Double pendingCutsPercentage;
    private String message;
}
