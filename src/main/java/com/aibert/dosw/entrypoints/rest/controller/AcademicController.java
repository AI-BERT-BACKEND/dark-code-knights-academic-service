package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.AcademicSummaryDTO;
import com.aibert.dosw.application.dto.response.AcademicWeightDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicSummaryUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.OptionalDouble;

@Tag(name = "Academic Dashboard", description = "Manage the student's academic summary: subjects, cut averages, overall GPA, and inter-service academic weight. (AIB-9)")
@RestController
@RequiredArgsConstructor
public class AcademicController {

    private final GetAcademicSummaryUseCase getAcademicSummaryUseCase;
    private final SubjectMapper subjectMapper;
    private final AverageCalculator averageCalculator;

    @Operation(
            summary = "Get academic summary",
            description = """
                    Returns a complete academic summary for the authenticated student: all subjects with \
                    their per-cut averages, each subject's overall weighted average, and a global GPA. \
                    If the student has no subjects the response is successful with an empty list. \
                    Subjects without grades return overallAverage: null and are excluded from the GPA calculation.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Academic summary returned (empty subjects list if the student has no subjects registered)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    @GetMapping("/api/v1/academic/summary")
    public ResponseEntity<ApiResponse<AcademicSummaryDTO>> getSummary(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId) {

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

    @Operation(
            summary = "Get academic weight for a subject",
            description = """
                    **Internal endpoint — called by engine-planning service via OpenFeign.** \
                    Returns the overall weighted average of a specific subject identified by its external UUID. \
                    Returns academicWeight: null if the subject has no grades registered yet. \
                    This endpoint does not enforce student ownership: it is designed for inter-service consumption.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Academic weight returned (null if the subject has no grades yet)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    @GetMapping("/api/v1/academic/weight")
    public ResponseEntity<ApiResponse<AcademicWeightDTO>> getAcademicWeight(
            @Parameter(description = "Student ID whose subjects are queried", required = true)
            @RequestParam String studentId,
            @Parameter(description = "External UUID of the subject (as provided by the planning service)", required = true,
                    example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
            @RequestParam String subjectId) {

        List<Subject> subjects = getAcademicSummaryUseCase.getSummary(studentId);

        Double weight = subjects.stream()
                .filter(s -> subjectId.equals(s.getExternalId()))
                .findFirst()
                .map(s -> averageCalculator.calculateOverallAverage(s.getEvaluationCuts()))
                .orElse(null);

        return ResponseEntity.ok(ApiResponse.ok(
                AcademicWeightDTO.builder().academicWeight(weight).build(), "ok"));
    }
}
