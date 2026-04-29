package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.AcademicSummaryDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicSummaryUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.OptionalDouble;

@RestController
@RequiredArgsConstructor
public class AcademicController {

    private final GetAcademicSummaryUseCase getAcademicSummaryUseCase;
    private final SubjectMapper subjectMapper;

    @GetMapping("/api/v1/academic/summary")
    public ResponseEntity<ApiResponse<AcademicSummaryDTO>> getSummary(
            @RequestHeader("X-Student-Id") String studentId) {

        List<Subject> subjects = getAcademicSummaryUseCase.getSummary(studentId);

        List<AveragesResponseDTO> subjectSummaries = subjects.stream()
                .map(subject -> {
                    boolean anyGraded = subject.getEvaluationCuts().stream()
                            .anyMatch(c -> c.getGrade() != null);
                    Double overallAverage = anyGraded
                            ? subject.getEvaluationCuts().stream()
                                    .filter(c -> c.getGrade() != null)
                                    .mapToDouble(c -> c.getGrade() * c.getCutPercentage())
                                    .sum() / 100.0
                            : null;
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
