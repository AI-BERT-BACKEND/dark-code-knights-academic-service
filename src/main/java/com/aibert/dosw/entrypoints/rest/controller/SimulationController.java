package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SimulationRequestDTO;
import com.aibert.dosw.application.dto.response.SimulationResponseDTO;
import com.aibert.dosw.domain.model.SimulationResult;
import com.aibert.dosw.domain.ports.in.SimulateTargetGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class SimulationController {

    private final SimulateTargetGradeUseCase simulateTargetGradeUseCase;

    @PostMapping("/api/v1/subjects/{subjectId}/simulate")
    public ResponseEntity<ApiResponse<SimulationResponseDTO>> simulate(
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
