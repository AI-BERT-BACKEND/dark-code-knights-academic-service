package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.AcademicGoalRequestDTO;
import com.aibert.dosw.application.dto.response.AcademicGoalProgressDTO;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.AcademicGoalProgress;
import com.aibert.dosw.domain.ports.in.GetAcademicGoalUseCase;
import com.aibert.dosw.domain.ports.in.SetAcademicGoalUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Academic Goals", description = "Set and track target grades per subject (AIB-11)")
@RestController
@RequiredArgsConstructor
public class AcademicGoalController {

    private final SetAcademicGoalUseCase setAcademicGoalUseCase;
    private final GetAcademicGoalUseCase getAcademicGoalUseCase;

    @Operation(
            summary = "Set or update academic goal",
            description = "Creates or replaces the target grade for the given subject. "
                    + "Calling this endpoint again with a new value updates the existing goal."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Goal saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Invalid target grade"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Subject not found or does not belong to the student")
    })
    @PutMapping("/api/v1/subjects/{subjectId}/goal")
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> setGoal(
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody AcademicGoalRequestDTO request) {

        AcademicGoal saved = setAcademicGoalUseCase.set(subjectId, studentId, request.getTargetGrade());
        AcademicGoalProgress progress = getAcademicGoalUseCase.getProgress(saved.getSubjectId(), studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress), "Meta académica guardada exitosamente"));
    }

    @Operation(
            summary = "Get goal and progress for a subject",
            description = "Returns the stored target grade together with the student's current average, "
                    + "the grade required across pending cuts, and whether the goal is still achievable."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Goal and progress returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Goal or subject not found")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/goal")
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> getGoal(
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId) {

        AcademicGoalProgress progress = getAcademicGoalUseCase.getProgress(subjectId, studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress)));
    }

    @Operation(
            summary = "Get all goals for a semester",
            description = "Returns the progress snapshot for every subject that has a goal set "
                    + "in the specified semester. Returns an empty list when no goals exist."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Goals returned (may be empty)")
    })
    @GetMapping("/api/v1/academic/goals")
    public ResponseEntity<ApiResponse<List<AcademicGoalProgressDTO>>> getAllGoals(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "Semester filter (e.g. 2025-1)", required = true)
            @RequestParam String semester) {

        List<AcademicGoalProgressDTO> dtos = getAcademicGoalUseCase.getAllProgress(studentId, semester)
                .stream()
                .map(this::toDTO)
                .toList();

        String message = dtos.isEmpty()
                ? "No se encontraron metas académicas para el semestre " + semester
                : "ok";
        return ResponseEntity.ok(ApiResponse.ok(dtos, message));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private AcademicGoalProgressDTO toDTO(AcademicGoalProgress p) {
        return AcademicGoalProgressDTO.builder()
                .goalId(p.getGoalId())
                .subjectId(p.getSubjectId())
                .subjectName(p.getSubjectName())
                .semester(p.getSemester())
                .targetGrade(p.getTargetGrade())
                .currentAverage(p.getCurrentAverage())
                .requiredGrade(p.getRequiredGrade())
                .isAchievable(p.isAchievable())
                .build();
    }
}
