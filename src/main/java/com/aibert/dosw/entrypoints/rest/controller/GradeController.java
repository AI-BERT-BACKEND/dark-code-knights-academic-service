package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.GradeRequestDTO;
import com.aibert.dosw.application.dto.request.UpdateGradeRequestDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.dto.response.GradeDeleteResponseDTO;
import com.aibert.dosw.application.dto.response.GradeResponseDTO;
import com.aibert.dosw.application.mapper.GradeMapper;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.DeleteGradeUseCase;
import com.aibert.dosw.domain.ports.in.GetSubjectsUseCase;
import com.aibert.dosw.domain.ports.in.RegisterGradeUseCase;
import com.aibert.dosw.domain.ports.in.UpdateGradeUseCase;
import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Grades", description = "Manage grade activities inside evaluation cuts: register, list, update, delete, and compute averages. (AIB-15)")
@RestController
@RequiredArgsConstructor
public class GradeController {

    private final RegisterGradeUseCase registerGradeUseCase;
    private final UpdateGradeUseCase updateGradeUseCase;
    private final DeleteGradeUseCase deleteGradeUseCase;
    private final GetSubjectsUseCase getSubjectsUseCase;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;
    private final AverageCalculator averageCalculator;

    @Operation(
            summary = "Register a grade",
            description = """
                    Registers an evaluation activity (grade) inside a specific cut of a subject. \
                    The sum of all activity percentages within the same cut must not exceed 100 — \
                    returns 422 if exceeded. Returns 422 if gradeValue is outside the 0.0–5.0 range. \
                    Returns 404 if the subject or cut is not found. \
                    Automatically recalculates the cut's weighted average after registration.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Grade registered and cut average recalculated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: missing required field or invalid percentage"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject or evaluation cut not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Grade value outside 0.0–5.0, or adding this activity would exceed 100% for the cut")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> register(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Parameter(description = "Numeric ID of the evaluation cut", example = "5",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long cutId,
            @Valid @RequestBody GradeRequestDTO request) {

        Grade saved = registerGradeUseCase.register(subjectId, cutId, gradeMapper.toDomain(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(gradeMapper.toResponseDTO(saved)));
    }

    @Operation(
            summary = "List grades for a cut",
            description = "Returns all evaluation activities registered in the specified evaluation cut. " +
                    "Returns an empty list if no activities have been registered yet for that cut.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of grades (may be empty)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject or evaluation cut not found")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByCut(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Parameter(description = "Numeric ID of the evaluation cut", example = "5",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long cutId) {

        List<Grade> grades = registerGradeUseCase.getGradesByCut(subjectId, cutId);
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTOList(grades)));
    }

    @Operation(
            summary = "Update a grade",
            description = """
                    Updates an existing grade activity (value, name, or percentage). \
                    Automatically recalculates the cut's weighted average and the subject's overall average. \
                    Returns 422 if the new gradeValue is outside 0.0–5.0 or if the updated percentage \
                    would cause the cut's total to exceed 100%. Returns 404 if the grade is not found.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Grade updated and averages recalculated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: missing required field"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject, cut, or grade not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Grade value outside 0.0–5.0 or percentage would exceed 100% for the cut")
    })
    @PutMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> update(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Parameter(description = "Numeric ID of the evaluation cut", example = "5",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long cutId,
            @Parameter(description = "Numeric ID of the grade to update", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequestDTO request) {

        Grade updated = updateGradeUseCase.update(subjectId, cutId, gradeId, gradeMapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Delete a grade",
            description = "Deletes a grade activity from an evaluation cut. " +
                    "Automatically recalculates the cut's weighted average and the subject's overall average after deletion. " +
                    "Returns 404 if the grade or its parent subject/cut is not found.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Grade deleted and averages recalculated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject, cut, or grade not found")
    })
    @DeleteMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeDeleteResponseDTO>> delete(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Parameter(description = "Numeric ID of the evaluation cut", example = "5",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long cutId,
            @Parameter(description = "Numeric ID of the grade to delete", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long gradeId) {

        Double updatedAverage = deleteGradeUseCase.delete(subjectId, cutId, gradeId);
        GradeDeleteResponseDTO responseDTO = GradeDeleteResponseDTO.builder()
                .message("Nota eliminada exitosamente!")
                .success(true)
                .updatedAverage(updatedAverage)
                .build();
        return ResponseEntity.ok(ApiResponse.ok(responseDTO));
    }

    @Operation(
            summary = "Get subject averages",
            description = "Returns the weighted average for each evaluation cut and the overall subject average. " +
                    "Cuts without any registered grades return grade: null and are excluded from the overall average calculation. " +
                    "Formula: overallAverage = Σ(cut.grade × cut.cutPercentage) / 100.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Averages returned (overallAverage is null if no cuts have been graded yet)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/averages")
    public ResponseEntity<ApiResponse<AveragesResponseDTO>> getAverages(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId) {

        Subject subject = getSubjectsUseCase.getById(subjectId);

        Double overallAverage = averageCalculator.calculateOverallAverage(subject.getEvaluationCuts());

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
