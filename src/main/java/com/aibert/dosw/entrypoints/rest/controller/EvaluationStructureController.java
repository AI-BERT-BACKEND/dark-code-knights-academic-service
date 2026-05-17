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

@Tag(name = "Evaluation Structure", description = "Configuration of evaluation cuts for a subject (R07)")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class EvaluationStructureController {

    private final ConfigureEvaluationStructureUseCase configureEvaluationStructureUseCase;
    private final SubjectMapper subjectMapper;

    @Operation(
            summary = "Configure evaluation structure",
            description = "Completely replaces the subject's evaluation cuts. Locked if grades have already been registered."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Structure configured successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Percentage sum ≠ 100 or empty list"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Structure locked because grades have already been registered")
    })
    @PutMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> configure(
            @Parameter(description = "Subject ID", required = true)
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
            summary = "Get evaluation structure",
            description = "Returns the subject's current evaluation cuts with their calculated averages."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evaluation structure found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> getStructure(
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId) {

        List<EvaluationCut> cuts = configureEvaluationStructureUseCase.getStructure(subjectId);

        EvaluationStructureResponseDTO response = EvaluationStructureResponseDTO.builder()
                .subjectId(subjectId)
                .evaluationCuts(subjectMapper.toResponseCutDTOList(cuts))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
