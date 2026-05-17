package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.AcademicSummaryDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicSummaryUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.OptionalDouble;

@Tag(name = "Academic Dashboard", description = "Student academic summary (R05)")
@RestController
@RequiredArgsConstructor
public class AcademicController {

    private final GetAcademicSummaryUseCase getAcademicSummaryUseCase;
    private final SubjectMapper subjectMapper;
    private final AverageCalculator averageCalculator;

    @Operation(
            summary = "Get academic summary",
            description = "Returns all student subjects with averages per cut, overall average per subject, and global GPA."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Academic summary generated (may have an empty list if there are no subjects)")
    })
    @GetMapping("/api/v1/academic/summary")
    public ResponseEntity<ApiResponse<AcademicSummaryDTO>> getSummary(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId) {

        List<Subject> subjects = getAcademicSummaryUseCase.getSummary(studentId);

        List<AveragesResponseDTO> subjectSummaries = subjects.stream()
                .map(subject -> {
                    Double overallAverage = averageCalculator.calculateOverallAverage(subject.getEvaluationCuts());
                    return AveragesResponseDTO.builder()
                            .subjectId(subject.getId())
                            .subjectName(subject.getSubjectName())
                            .semester(subject.getSemester())
                            .overallAverage(overallAverage)
                            .cuts(subjectMapper.toResponseCutDTOList(subject.getEvaluationCuts()))
                            .build();
                })
                .toList();

        OptionalDouble gpa = subjectSummaries.stream()
                .filter(s -> s.getOverallAverage() != null)
                .mapToDouble(AveragesResponseDTO::getOverallAverage)
                .average();
        Double academicGpa = gpa.isPresent() ? gpa.getAsDouble() : null;

        AcademicSummaryDTO summary = AcademicSummaryDTO.builder()
                .studentId(studentId)
                .academicGpa(academicGpa)
                .subjects(subjectSummaries)
                .build();

        String message = subjects.isEmpty() ? "El estudiante no tiene materias registradas" : "ok";
        return ResponseEntity.ok(ApiResponse.ok(summary, message));
    }
}
