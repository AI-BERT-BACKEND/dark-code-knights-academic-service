package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.EvaluationStructureRequestDTO;
import com.aibert.dosw.application.dto.response.EvaluationStructureResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.ports.in.ConfigureEvaluationStructureUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
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

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class EvaluationStructureController {

    private final ConfigureEvaluationStructureUseCase configureEvaluationStructureUseCase;
    private final SubjectMapper subjectMapper;

    @PutMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> configure(
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

    @GetMapping("/{subjectId}/evaluation-structure")
    public ResponseEntity<ApiResponse<EvaluationStructureResponseDTO>> getStructure(
            @PathVariable Long subjectId) {

        List<EvaluationCut> cuts = configureEvaluationStructureUseCase.getStructure(subjectId);

        EvaluationStructureResponseDTO response = EvaluationStructureResponseDTO.builder()
                .subjectId(subjectId)
                .evaluationCuts(subjectMapper.toResponseCutDTOList(cuts))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
