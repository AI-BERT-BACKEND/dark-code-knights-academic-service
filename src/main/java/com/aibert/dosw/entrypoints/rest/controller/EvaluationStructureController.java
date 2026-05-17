package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.EvaluationStructureRequestDTO;
import com.aibert.dosw.application.dto.response.EvaluationStructureResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.ports.in.ConfigureEvaluationStructureUseCase;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Estructura de Evaluación", description = "Configuración de cortes evaluativos de una materia (R07)")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class EvaluationStructureController {

    private final ConfigureEvaluationStructureUseCase configureEvaluationStructureUseCase;
    private final SubjectMapper subjectMapper;

    @Operation(
            summary = "Configurar estructura de evaluación",
            description = "Reemplaza completamente los cortes de la materia. Bloqueado si ya hay notas registradas."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estructura configurada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Suma de porcentajes ≠ 100 o lista vacía"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Estructura bloqueada porque ya existen notas registradas")
    })
    @PutMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> configure(
            @Parameter(description = "ID de la materia", required = true)
            @PathVariable Long subjectId,
            @Valid @RequestBody EvaluationStructureRequestDTO request) {

        List<EvaluationCut> cuts = subjectMapper.toDomainCutList(request.getEvaluationCuts());
        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(subjectId, cuts);

        EvaluationStructureResponseDTO response = EvaluationStructureResponseDTO.builder()
                .subjectId(subjectId)
                .evaluationCuts(subjectMapper.toResponseCutDTOList(result))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(
            summary = "Consultar estructura de evaluación",
            description = "Retorna los cortes actuales de la materia con sus promedios calculados."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estructura de evaluación encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada")
    })
    @GetMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> getStructure(
            @Parameter(description = "ID de la materia", required = true)
            @PathVariable Long subjectId) {

        List<EvaluationCut> cuts = configureEvaluationStructureUseCase.getStructure(subjectId);

        EvaluationStructureResponseDTO response = EvaluationStructureResponseDTO.builder()
                .subjectId(subjectId)
                .evaluationCuts(subjectMapper.toResponseCutDTOList(cuts))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
