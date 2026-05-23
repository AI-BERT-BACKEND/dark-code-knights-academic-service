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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Academic Goals", description = "Manage academic goals for the student: create, update, and track progress toward a target grade. (AIB-11)")
@RestController
@RequestMapping("/api/v1/academic/goals")
@RequiredArgsConstructor
public class AcademicGoalController {

    private final SetAcademicGoalUseCase setAcademicGoalUseCase;
    private final GetAcademicGoalUseCase getAcademicGoalUseCase;

    @PutMapping
    @Operation(
            summary = "Create or update an academic goal",
            description = """
                    Creates a new academic goal or updates an existing one with the same name (upsert by goalName). \
                    A goal can be either general (subjectId: null) or linked to a specific subject. \
                    Returns 404 if subjectId is provided but the subject does not exist.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal saved and progress returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: goalName too short, targetGrade out of range 0.0–5.0"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject referenced by subjectId not found")
    })
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> setGoal(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Valid @RequestBody AcademicGoalRequestDTO request) {

        log.info("setGoal - studentId={}", studentId);
        AcademicGoal saved = setAcademicGoalUseCase.set(
                studentId,
                request.getGoalName(),
                request.getTargetGrade(),
                request.getSubjectId());

        AcademicGoalProgress progress = getAcademicGoalUseCase.getById(saved.getId(), studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress), "¡Meta guardada exitosamente!"));
    }

    @GetMapping("/{goalId}")
    @Operation(
            summary = "Get an academic goal by ID",
            description = "Returns the progress details of a specific academic goal, including the student's current " +
                    "average in the linked subject and whether the target grade is still achievable. " +
                    "Returns 404 if the goal does not exist or does not belong to the authenticated student.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal progress returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found or does not belong to the student")
    })
    public ResponseEntity<ApiResponse<AcademicGoalProgressDTO>> getGoal(
            @Parameter(description = "Numeric ID of the academic goal", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long goalId,
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId) {

        log.info("getGoal - studentId={}, goalId={}", studentId, goalId);
        AcademicGoalProgress progress = getAcademicGoalUseCase.getById(goalId, studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(progress)));
    }

    @GetMapping
    @Operation(
            summary = "List all goals with progress",
            description = "Returns all academic goals for the authenticated student with real-time progress data. " +
                    "Use the optional semester query parameter to filter by subjects in a specific semester (e.g. 2025-1). " +
                    "General goals (not linked to any subject) are always included regardless of the filter.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of goals with progress (may be empty)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public ResponseEntity<ApiResponse<List<AcademicGoalProgressDTO>>> getAllGoals(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Parameter(description = "Optional semester filter in YYYY-1 or YYYY-2 format", example = "2025-1")
            @RequestParam(required = false) String semester) {

        log.info("getAllGoals - studentId={}, semester={}", studentId, semester);
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
