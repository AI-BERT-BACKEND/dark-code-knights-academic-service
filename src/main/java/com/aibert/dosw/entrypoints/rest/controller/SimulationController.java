package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SimulationRequestDTO;
import com.aibert.dosw.application.dto.response.PendingCutSimulationDTO;
import com.aibert.dosw.application.dto.response.SimulationResponseDTO;
import com.aibert.dosw.domain.model.SimulationResult;
import com.aibert.dosw.domain.ports.in.SimulateTargetGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@Tag(name = "Simulation", description = "Simulate the grade needed in pending evaluation cuts to reach a target final grade. (AIB-17)")
@RestController
@RequiredArgsConstructor
public class SimulationController {

    private final SimulateTargetGradeUseCase simulateTargetGradeUseCase;

    @Operation(
            summary = "Simulate target grade",
            description = """
                    Calculates the minimum grade needed across all pending (ungraded) evaluation cuts \
                    to reach the given target final grade. \
                    Formula: requiredGrade = (targetGrade × 100 − currentScore) / pendingPercentage. \
                    If requiredGrade > 5.0, the goal is not achievable (isAchievable: false). \
                    If requiredGrade ≤ 0.0, the goal is already secured regardless of pending cuts (requiredGrade clamped to 0.0). \
                    Returns 422 if the subject has no pending cuts to grade.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Simulation calculated — check isAchievable to determine feasibility",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "requiredGrade": 3.625,
                                        "isAchievable": true,
                                        "message": "Para alcanzar 4.0 necesitas obtener 3.63 o más en los cortes pendientes (40% restante).",
                                        "pendingCuts": [
                                          { "cutId": 7, "cutName": "Corte 3", "cutPercentage": 40, "requiredGrade": 3.625 }
                                        ]
                                      },
                                      "message": null
                                    }"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "targetGrade is null, below 0.0, or above 5.0"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "The subject has no pending (ungraded) cuts — nothing to simulate")
    })
    @PostMapping("/api/v1/subjects/{subjectId}/simulate")
    public ResponseEntity<ApiResponse<SimulationResponseDTO>> simulate(
            @Parameter(description = "Numeric ID of the subject to simulate", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
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
