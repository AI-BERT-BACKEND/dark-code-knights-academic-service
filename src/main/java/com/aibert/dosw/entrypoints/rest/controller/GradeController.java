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

@Tag(name = "Notas", description = "Registro y gestión de notas por corte de evaluación (R08, R09)")
@RestController
@RequiredArgsConstructor
public class GradeController {

    private final RegisterGradeUseCase registerGradeUseCase;
    private final UpdateGradeUseCase updateGradeUseCase;
    private final DeleteGradeUseCase deleteGradeUseCase;
    private final GetSubjectsUseCase getSubjectsUseCase;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;

    @Operation(
            summary = "Registrar nota",
            description = "Registra una actividad evaluativa en un corte. La suma de porcentajes del corte no puede superar 100."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Nota registrada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Nota fuera de rango 0.0–5.0 o porcentaje del corte superaría 100%")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> register(
            @Parameter(description = "ID de la materia", required = true) @PathVariable Long subjectId,
            @Parameter(description = "ID del corte de evaluación", required = true) @PathVariable Long cutId,
            @Valid @RequestBody GradeRequestDTO request) {

        Grade saved = registerGradeUseCase.register(subjectId, cutId, gradeMapper.toDomain(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(gradeMapper.toResponseDTO(saved)));
    }

    @Operation(
            summary = "Listar notas de un corte",
            description = "Retorna todas las actividades evaluativas registradas en el corte indicado."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de notas (puede ser vacía)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByCut(
            @Parameter(description = "ID de la materia", required = true) @PathVariable Long subjectId,
            @Parameter(description = "ID del corte de evaluación", required = true) @PathVariable Long cutId) {

        List<Grade> grades = registerGradeUseCase.getGradesByCut(subjectId, cutId);
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTOList(grades)));
    }

    @Operation(
            summary = "Editar nota",
            description = "Actualiza una nota existente y recalcula automáticamente el promedio del corte y de la materia."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Nota o materia no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Nota fuera de rango o porcentaje inválido")
    })
    @PutMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeResponseDTO>> update(
            @Parameter(description = "ID de la materia", required = true) @PathVariable Long subjectId,
            @Parameter(description = "ID del corte de evaluación", required = true) @PathVariable Long cutId,
            @Parameter(description = "ID de la nota", required = true) @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequestDTO request) {

        Grade updated = updateGradeUseCase.update(subjectId, cutId, gradeId, gradeMapper.toDomain(request));
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Eliminar nota",
            description = "Elimina una nota y recalcula automáticamente los promedios del corte y de la materia."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nota eliminada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Nota o materia no encontrada")
    })
    @DeleteMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID de la materia", required = true) @PathVariable Long subjectId,
            @Parameter(description = "ID del corte de evaluación", required = true) @PathVariable Long cutId,
            @Parameter(description = "ID de la nota", required = true) @PathVariable Long gradeId) {

        deleteGradeUseCase.delete(subjectId, cutId, gradeId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Nota eliminada exitosamente"));
    }

    @Operation(
            summary = "Consultar promedios de la materia",
            description = "Retorna el promedio de cada corte y el promedio general ponderado de la materia."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promedios calculados exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    @GetMapping("/api/v1/subjects/{subjectId}/averages")
    public ResponseEntity<ApiResponse<AveragesResponseDTO>> getAverages(
            @Parameter(description = "ID de la materia", required = true)
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
