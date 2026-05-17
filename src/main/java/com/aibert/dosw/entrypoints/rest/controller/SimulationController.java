package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SimulationRequestDTO;
import com.aibert.dosw.application.dto.response.SimulationResponseDTO;
import com.aibert.dosw.domain.model.SimulationResult;
import com.aibert.dosw.domain.ports.in.SimulateTargetGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Tag(name = "Simulación", description = "Simulación de nota objetivo en cortes pendientes (R10)")
@RestController
@RequiredArgsConstructor
public class SimulationController {

    private final SimulateTargetGradeUseCase simulateTargetGradeUseCase;

    @Operation(
            summary = "Simular nota objetivo",
            description = "Calcula la nota mínima necesaria en los cortes pendientes para alcanzar la meta. Fórmula: requiredGrade = (targetGrade × 100 − puntajeActual) / porcentajePendiente"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Simulación calculada (puede ser alcanzable o no)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "targetGrade nulo o fuera del rango 0.0–5.0"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Materia no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "La materia no tiene cortes pendientes por calificar")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/simulate")
    public ResponseEntity<ApiResponse<SimulationResponseDTO>> simulate(
            @Parameter(description = "ID de la materia a simular", required = true)
            @PathVariable Long subjectId,
            @Valid @RequestBody SimulationRequestDTO request) {

        SimulationResult result = simulateTargetGradeUseCase.simulate(subjectId, request.getTargetGrade());

        String message;
        if (!result.isAchievable()) {
            message = String.format(
                    Locale.forLanguageTag("es"),
                    "No es posible alcanzar %.1f. La nota requerida (%.2f) supera el máximo permitido (5.0).",
                    result.getTargetGrade(), result.getRequiredGrade());
        } else if (result.getRequiredGrade() == 0.0) {
            message = String.format(
                    Locale.forLanguageTag("es"),
                    "¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás %.1f.",
                    result.getTargetGrade());
        } else {
            message = String.format(
                    Locale.forLanguageTag("es"),
                    "Para alcanzar %.1f necesitas obtener %.2f o más en los cortes pendientes (%.0f%% restante).",
                    result.getTargetGrade(), result.getRequiredGrade(), result.getPendingPercentage());
        }

        SimulationResponseDTO response = SimulationResponseDTO.builder()
                .targetGrade(result.getTargetGrade())
                .requiredGrade(result.getRequiredGrade())
                .achievable(result.isAchievable())
                .pendingCutsPercentage(result.getPendingPercentage())
                .message(message)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
