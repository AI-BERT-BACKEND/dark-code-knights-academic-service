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

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> create(
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject created = createSubjectUseCase.create(subject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(subjectMapper.toResponseDTO(created)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getAll(
            @RequestHeader("X-Student-Id") String studentId) {
        List<Subject> subjects = getSubjectsUseCase.getAllByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTOList(subjects)));
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getById(
            @PathVariable Long subjectId) {
        Subject subject = getSubjectsUseCase.getById(subjectId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(subject)));
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> update(
            @PathVariable Long subjectId,
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject updated = updateSubjectUseCase.update(subjectId, subject);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(updated)));
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long subjectId) {
        deleteSubjectUseCase.delete(subjectId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Materia eliminada exitosamente"));
    }
}
