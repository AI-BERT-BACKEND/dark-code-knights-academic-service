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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Subjects", description = "Academic subject management for the authenticated student (R06)")
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

    @Operation(
            summary = "Create subject",
            description = "Creates a new subject with its evaluation cuts. The sum of cutPercentage must be exactly 100."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Subject created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or incorrect percentage sum"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A subject with that name already exists in the same semester")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> create(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject created = createSubjectUseCase.create(subject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(subjectMapper.toResponseDTO(created)));
    }

    @Operation(
            summary = "List student subjects",
            description = "Returns all subjects registered by the authenticated student."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of subjects (may be empty)")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> getAll(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId) {
        List<Subject> subjects = getSubjectsUseCase.getAllByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTOList(subjects)));
    }

    @Operation(
            summary = "Get subject by ID",
            description = "Returns the full detail of a subject, including its evaluation cuts and averages."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subject found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Subject not found or does not belong to the student")
    })
    @GetMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getById(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId) {
        Subject subject = getSubjectsUseCase.getByIdAndStudent(subjectId, studentId);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(subject)));
    }

    @Operation(
            summary = "Update subject",
            description = "Updates the data of an existing subject. Also replaces its evaluation cuts."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subject updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or incorrect percentages"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate subject name in the same semester")
    })
    @PutMapping("/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> update(
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject updated = updateSubjectUseCase.update(subjectId, subject);
        return ResponseEntity.ok(ApiResponse.ok(subjectMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Delete subject",
            description = "Deletes the subject along with all its associated cuts and grades."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Subject not found or does not belong to the student")
    })
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "Subject ID", required = true)
            @PathVariable Long subjectId) {
        deleteSubjectUseCase.delete(subjectId, studentId);
        return ResponseEntity.noContent().build();
    }
}
