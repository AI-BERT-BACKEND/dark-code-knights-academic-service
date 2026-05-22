package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SimulationRequestDTO;
import com.aibert.dosw.application.dto.response.PendingCutSimulationDTO;
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

import java.util.List;
import java.util.Locale;

@Tag(name = "Simulation", description = "Target grade simulation for pending evaluation cuts (AIB-17)")
@RestController
@RequiredArgsConstructor
public class SimulationController {

    private final SimulateTargetGradeUseCase simulateTargetGradeUseCase;

    @Operation(
            summary = "Simulate target grade",
            description = "Calculates the minimum grade needed in the pending cuts to reach the target. Formula: requiredGrade = (targetGrade × 100 − currentScore) / pendingPercentage"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Simulation calculated (may or may not be achievable)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "targetGrade is null or out of range 0.0–5.0"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "The subject has no pending cuts to grade")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/simulate")
    public ResponseEntity<ApiResponse<SimulationResponseDTO>> simulate(
            @Parameter(description = "ID of the subject to simulate", required = true)
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

        List<PendingCutSimulationDTO> pendingCutDTOs = result.getPendingCuts().stream()
                .map(cut -> PendingCutSimulationDTO.builder()
                        .cutId(cut.getId())
                        .cutName(cut.getCutName())
                        .cutPercentage(cut.getCutPercentage())
                        .requiredGrade(result.getRequiredGrade())
                        .build())
                .toList();

        SimulationResponseDTO response = SimulationResponseDTO.builder()
                .requiredGrade(result.getRequiredGrade())
                .isAchievable(result.isAchievable())
                .message(message)
                .pendingCuts(pendingCutDTOs)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
