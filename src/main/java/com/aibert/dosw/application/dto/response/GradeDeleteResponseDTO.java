package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDeleteResponseDTO {

    private String message;
    private Boolean success;
    private Double updatedAverage;
}
