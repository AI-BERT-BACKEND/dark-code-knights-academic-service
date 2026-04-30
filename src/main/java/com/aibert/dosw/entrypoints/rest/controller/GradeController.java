package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.GradeRequestDTO;
import com.aibert.dosw.application.dto.request.UpdateGradeRequestDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.dto.response.GradeResponseDTO;
import com.aibert.dosw.application.mapper.GradeMapper;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.DeleteGradeUseCase;
import com.aibert.dosw.domain.ports.in.GetSubjectsUseCase;
import com.aibert.dosw.domain.ports.in.RegisterGradeUseCase;
import com.aibert.dosw.domain.ports.in.UpdateGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GradeController {

    private final RegisterGradeUseCase registerGradeUseCase;
    private final UpdateGradeUseCase updateGradeUseCase;
    private final DeleteGradeUseCase deleteGradeUseCase;
    private final GetSubjectsUseCase getSubjectsUseCase;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;

    @PostMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> register(
            @PathVariable Long subjectId,
            @PathVariable Long cutId,
            @Valid @RequestBody GradeRequestDTO request) {

        Grade saved = registerGradeUseCase.register(subjectId, cutId, gradeMapper.toDomain(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(gradeMapper.toResponseDTO(saved)));
    }

    @GetMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByCut(
            @PathVariable Long subjectId,
            @PathVariable Long cutId) {

        List<Grade> grades = registerGradeUseCase.getGradesByCut(subjectId, cutId);
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTOList(grades)));
    }

    @PutMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> update(
            @PathVariable Long subjectId,
            @PathVariable Long cutId,
            @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequestDTO request) {

        Grade updated = updateGradeUseCase.update(subjectId, cutId, gradeId, gradeMapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTO(updated)));
    }

    @DeleteMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long subjectId,
            @PathVariable Long cutId,
            @PathVariable Long gradeId) {

        deleteGradeUseCase.delete(subjectId, cutId, gradeId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Nota eliminada exitosamente"));
    }

    @GetMapping("/api/v1/subjects/{subjectId}/averages")
    public ResponseEntity<ApiResponse<AveragesResponseDTO>> getAverages(
            @PathVariable Long subjectId) {

        Subject subject = getSubjectsUseCase.getById(subjectId);

        boolean anyGraded = subject.getEvaluationCuts().stream()
                .anyMatch(c -> c.getGrade() != null);
        Double overallAverage = anyGraded
                ? subject.getEvaluationCuts().stream()
                        .filter(c -> c.getGrade() != null)
                        .mapToDouble(c -> c.getGrade() * c.getCutPercentage())
                        .sum() / 100.0
                : null;

        AveragesResponseDTO response = AveragesResponseDTO.builder()
                .subjectId(subject.getId())
                .subjectName(subject.getSubjectName())
                .semester(subject.getSemester())
                .overallAverage(overallAverage)
                .cuts(subjectMapper.toResponseCutDTOList(subject.getEvaluationCuts()))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
