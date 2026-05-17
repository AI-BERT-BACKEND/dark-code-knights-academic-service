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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "Grades", description = "Grade registration and management by evaluation cut (R08, R09)")
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
            summary = "Register grade",
            description = "Registers an evaluation activity in a cut. The sum of the cut's percentages cannot exceed 100."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Grade registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Grade out of range 0.0–5.0 or cut percentage would exceed 100%")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> register(
            @Parameter(description = "Subject ID", required = true) @PathVariable Long subjectId,
            @Parameter(description = "Evaluation cut ID", required = true) @PathVariable Long cutId,
            @Valid @RequestBody GradeRequestDTO request) {

        Grade saved = registerGradeUseCase.register(subjectId, cutId, gradeMapper.toDomain(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(gradeMapper.toResponseDTO(saved)));
    }

    @Operation(
            summary = "List grades for a cut",
            description = "Returns all evaluation activities registered in the specified cut."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of grades (may be empty)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByCut(
            @Parameter(description = "Subject ID", required = true) @PathVariable Long subjectId,
            @Parameter(description = "Evaluation cut ID", required = true) @PathVariable Long cutId) {

        List<Grade> grades = registerGradeUseCase.getGradesByCut(subjectId, cutId);
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTOList(grades)));
    }

    @Operation(
            summary = "Update grade",
            description = "Updates an existing grade and automatically recalculates the cut and subject averages."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Grade updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Grade or subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Grade out of range or invalid percentage")
    })
    @PutMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> update(
            @Parameter(description = "Subject ID", required = true) @PathVariable Long subjectId,
            @Parameter(description = "Evaluation cut ID", required = true) @PathVariable Long cutId,
            @Parameter(description = "Grade ID", required = true) @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequestDTO request) {

        Grade updated = updateGradeUseCase.update(subjectId, cutId, gradeId, gradeMapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Delete grade",
            description = "Deletes a grade and automatically recalculates the cut and subject averages."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Grade deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Grade or subject not found")
    })
    @DeleteMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeDeleteResponseDTO>> delete(
            @Parameter(description = "Subject ID", required = true) @PathVariable Long subjectId,
            @Parameter(description = "Evaluation cut ID", required = true) @PathVariable Long cutId,
            @Parameter(description = "Grade ID", required = true) @PathVariable Long gradeId) {

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
            description = "Returns the average for each cut and the overall weighted average of the subject."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Averages calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/averages")
    public ResponseEntity<ApiResponse<AveragesResponseDTO>> getAverages(
            @Parameter(description = "Subject ID", required = true)
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
