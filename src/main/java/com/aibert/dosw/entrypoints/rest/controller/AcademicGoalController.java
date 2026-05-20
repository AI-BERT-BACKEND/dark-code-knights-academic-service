package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.AcademicGoalRequestDTO;
import com.aibert.dosw.application.dto.response.AcademicGoalProgressDTO;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.AcademicGoalProgress;
import com.aibert.dosw.domain.ports.in.GetAcademicGoalUseCase;
import com.aibert.dosw.domain.ports.in.SetAcademicGoalUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Academic Goals", description = "Academic goal definition and progress tracking (AIB-11)")
@RestController
@RequestMapping("/api/v1/academic/goals")
@RequiredArgsConstructor
public class AcademicGoalController {

    private final SetAcademicGoalUseCase setAcademicGoalUseCase;
    private final GetAcademicGoalUseCase getAcademicGoalUseCase;

    @PutMapping
    @Operation(summary = "Create or update an academic goal")
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> setGoal(
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody AcademicGoalRequestDTO request) {

        AcademicGoal saved = setAcademicGoalUseCase.set(
                studentId,
                request.getGoalName(),
                request.getTargetGrade(),
                request.getSubjectId());

        AcademicGoalProgress progress = getAcademicGoalUseCase.getById(saved.getId(), studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress), "¡Meta guardada exitosamente!"));
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "Get an academic goal by ID")
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> getGoal(
            @PathVariable Long goalId,
            @RequestHeader("X-Student-Id") String studentId) {

        AcademicGoalProgress progress = getAcademicGoalUseCase.getById(goalId, studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress)));
    }

    @GetMapping
    @Operation(summary = "List all goals for the student, with optional semester filter")
    public ResponseEntity<ApiResponse<List<AcademicGoalProgressDTO>>> getAllGoals(
            @RequestHeader("X-Student-Id") String studentId,
            @RequestParam(required = false) String semester) {

        List<AcademicGoalProgressDTO> dtos = getAcademicGoalUseCase.getAllProgress(studentId, semester)
                .stream()
                .map(this::toDTO)
                .toList();

        String message = dtos.isEmpty()
                ? "No se encontraron metas académicas"
                : "ok";
        return ResponseEntity.ok(ApiResponse.ok(dtos, message));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private AcademicGoalProgressDTO toDTO(AcademicGoalProgress p) {
        return AcademicGoalProgressDTO.builder()
                .goalId(p.getGoalId())
                .goalName(p.getGoalName())
                .subjectId(p.getSubjectId())
                .targetGrade(p.getTargetGrade())
                .currentAverage(p.getCurrentAverage())
                .requiredGrade(p.getRequiredGrade())
                .isAchievable(p.isAchievable())
                .build();
    }
}
