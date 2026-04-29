package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.GradeRequestDTO;
import com.aibert.dosw.application.dto.response.GradeResponseDTO;
import com.aibert.dosw.application.mapper.GradeMapper;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.ports.in.RegisterGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades")
@RequiredArgsConstructor
public class GradeController {

    private final RegisterGradeUseCase registerGradeUseCase;
    private final GradeMapper gradeMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponseDTO>> register(
            @PathVariable Long subjectId,
            @PathVariable Long cutId,
            @Valid @RequestBody GradeRequestDTO request) {

        Grade grade = gradeMapper.toDomain(request);
        Grade saved = registerGradeUseCase.register(subjectId, cutId, grade);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(gradeMapper.toResponseDTO(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeResponseDTO>>> getGradesByCut(
            @PathVariable Long subjectId,
            @PathVariable Long cutId) {

        List<Grade> grades = registerGradeUseCase.getGradesByCut(subjectId, cutId);
        return ResponseEntity.ok(ApiResponse.ok(gradeMapper.toResponseDTOList(grades)));
    }
}
