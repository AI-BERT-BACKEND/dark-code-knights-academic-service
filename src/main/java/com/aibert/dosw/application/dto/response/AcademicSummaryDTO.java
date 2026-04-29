package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicSummaryDTO {

    private String studentId;
    private Double academicGpa;
    private List<AveragesResponseDTO> subjects;
}
