package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCutSimulationDTO {

    private Long cutId;
    private String cutName;
    private Double cutPercentage;
    private Double requiredGrade;
}
