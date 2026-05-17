package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SubjectRequestDTO;
import com.aibert.dosw.application.dto.response.SubjectResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.CreateSubjectUseCase;
import com.aibert.dosw.domain.ports.in.DeleteSubjectUseCase;
import com.aibert.dosw.domain.ports.in.GetSubjectsUseCase;
import com.aibert.dosw.domain.ports.in.UpdateSubjectUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import com.aibert.dosw.entrypoints.rest.mapper.SubjectEntrypointMapper;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Materias", description = "Gestión de materias académicas del estudiante (R06)")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final CreateSubjectUseCase createSubjectUseCase;
    private final GetSubjectsUseCase getSubjectsUseCase;
    private final UpdateSubjectUseCase updateSubjectUseCase;
    private final DeleteSubjectUseCase deleteSubjectUseCase;
    private final SubjectEntrypointMapper entrypointMapper;
    private final SubjectMapper subjectMapper;

    @Operation(
            summary = "Crear materia",
            description = "Crea una nueva materia con sus cortes de evaluación. La suma de cutPercentage debe ser exactamente 100."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Materia creada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o suma de porcentajes incorrecta"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ya existe una materia con ese nombre en el mismo semestre")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> create(
            @Parameter(description = "ID del estudiante autenticado", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject created = createSubjectUseCase.create(subject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(subjectMapper.toResponseDTO(created)));
    }

    @Operation(
            summary = "Listar materias del estudiante",
            description = "Retorna todas las materias registradas por el estudiante autenticado."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de materias (puede ser vacía)")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getAll(
            @Parameter(description = "ID del estudiante autenticado", required = true)
            @RequestHeader("X-Student-Id") String studentId) {
        List<Subject> subjects = getSubjectsUseCase.getAllByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTOList(subjects)));
    }

    @Operation(
            summary = "Obtener materia por ID",
            description = "Retorna el detalle completo de una materia, incluyendo sus cortes y promedios."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Materia encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    @GetMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getById(
            @Parameter(description = "ID del estudiante autenticado", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "ID de la materia", required = true)
            @PathVariable Long subjectId) {
        Subject subject = getSubjectsUseCase.getByIdAndStudent(subjectId, studentId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(subject)));
    }

    @Operation(
            summary = "Actualizar materia",
            description = "Actualiza los datos de una materia existente. Reemplaza también sus cortes de evaluación."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Materia actualizada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o porcentajes incorrectos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nombre de materia duplicado en el semestre")
    })
    @PutMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> update(
            @Parameter(description = "ID de la materia", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "ID del estudiante autenticado", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject updated = updateSubjectUseCase.update(subjectId, subject);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Eliminar materia",
            description = "Elimina la materia junto con todos sus cortes y notas asociados."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Materia eliminada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del estudiante autenticado", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "ID de la materia", required = true)
            @PathVariable Long subjectId) {
        deleteSubjectUseCase.delete(subjectId, studentId);
        return ResponseEntity.noContent().build();
    }
}
