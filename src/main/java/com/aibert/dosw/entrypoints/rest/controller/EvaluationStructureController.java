package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.EvaluationStructureRequestDTO;
import com.aibert.dosw.application.dto.response.EvaluationStructureResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.ports.in.ConfigureEvaluationStructureUseCase;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Evaluation Structure", description = "Manage the evaluation cut structure of a subject: configure and retrieve cut weights. (AIB-14)")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class EvaluationStructureController {

    private final ConfigureEvaluationStructureUseCase configureEvaluationStructureUseCase;
    private final SubjectMapper subjectMapper;

    @Operation(
            summary = "Configure evaluation structure",
            description = """
                    Completely replaces the evaluation cut structure of a subject. Old cuts are deleted \
                    and new ones are created (orphanRemoval). The sum of all cutPercentage values must equal \
                    exactly 100. Returns 409 if any cut in the subject already has grades registered — \
                    the structure cannot be modified once grading has begun.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evaluation structure configured successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "subjectId": 1,
                                        "evaluationCuts": [
                                          { "id": 5, "cutName": "Corte 1", "cutPercentage": 30, "grade": null },
                                          { "id": 6, "cutName": "Corte 2", "cutPercentage": 30, "grade": null },
                                          { "id": 7, "cutName": "Corte 3", "cutPercentage": 40, "grade": null }
                                        ]
                                      },
                                      "message": null
                                    }"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Empty cut list or cut percentages do not sum to 100"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Evaluation structure is locked because one or more cuts already have grades registered")
    })
    @PutMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> configure(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Valid @RequestBody EvaluationStructureRequestDTO request) {

        log.info("configureEvaluationStructure - subjectId={}", subjectId);
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
            description = "Returns the current evaluation cut structure of a subject, including each cut's " +
                    "weighted average if grades have been registered. Cuts without grades return grade: null.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evaluation structure returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> getStructure(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId) {

        log.info("getEvaluationStructure - subjectId={}", subjectId);
        List<EvaluationCut> cuts = configureEvaluationStructureUseCase.getStructure(subjectId);

        EvaluationStructureResponseDTO response = EvaluationStructureResponseDTO.builder()
                .subjectId(subjectId)
                .evaluationCuts(subjectMapper.toResponseCutDTOList(cuts))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
