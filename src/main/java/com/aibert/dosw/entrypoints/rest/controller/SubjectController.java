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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Tag(name = "Subjects", description = "Manage the student's academic subjects: create, retrieve, update, and delete. (AIB-13)")
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
            summary = "Create a subject",
            description = """
                    Creates a new academic subject for the authenticated student, including its initial \
                    evaluation cut structure. The sum of all cutPercentage values must equal exactly 100. \
                    Returns 409 if a subject with the same name already exists for that student in the same semester.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Subject created successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 1,
                                        "subjectName": "Álgebra Lineal",
                                        "credits": 3,
                                        "teacherName": "Prof. Martínez",
                                        "semester": "2025-1",
                                        "schedule": "LUNES 08:00-10:00",
                                        "overallAverage": null,
                                        "evaluationCuts": [
                                          { "id": 1, "cutName": "Corte 1", "cutPercentage": 40, "grade": null },
                                          { "id": 2, "cutName": "Corte 2", "cutPercentage": 60, "grade": null }
                                        ]
                                      },
                                      "message": null
                                    }"""))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: invalid field or evaluation cut percentages do not sum to 100"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A subject with that name already exists for the student in the same semester")
    })
    @PostMapping
    public ResponseEntity<com.aibert.dosw.entrypoints.ApiResponse<SubjectResponseDTO>> create(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        log.info("createSubject - studentId={}", studentId);
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject created = createSubjectUseCase.create(subject);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(com.aibert.dosw.entrypoints.ApiResponse.ok(subjectMapper.toResponseDTO(created)));
    }

    @Operation(
            summary = "List all subjects",
            description = "Returns all academic subjects registered by the authenticated student. " +
                    "Returns an empty list if the student has no subjects yet.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of subjects (may be empty)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    @GetMapping
    public ResponseEntity<com.aibert.dosw.entrypoints.ApiResponse<List<SubjectResponseDTO>>> getAll(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId) {
        log.info("getSubjects - studentId={}", studentId);
        List<Subject> subjects = getSubjectsUseCase.getAllByStudent(studentId);
        return ResponseEntity.ok(com.aibert.dosw.entrypoints.ApiResponse.ok(subjectMapper.toResponseDTOList(subjects)));
    }

    @Operation(
            summary = "Get a subject by ID",
            description = "Returns the full detail of a subject including its evaluation cuts and per-cut averages. " +
                    "Returns 403 if the subject exists but belongs to a different student.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subject detail returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Subject does not belong to the authenticated student"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/{subjectId}")
    public ResponseEntity<com.aibert.dosw.entrypoints.ApiResponse<SubjectResponseDTO>> getById(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId) {
        log.info("getSubject - studentId={}, subjectId={}", studentId, subjectId);
        Subject subject = getSubjectsUseCase.getByIdAndStudent(subjectId, studentId);
        return ResponseEntity.ok(com.aibert.dosw.entrypoints.ApiResponse.ok(subjectMapper.toResponseDTO(subject)));
    }

    @Operation(
            summary = "Update a subject",
            description = """
                    Replaces all fields of an existing subject, including its evaluation cut structure. \
                    The structure update follows the same rules as PUT /evaluation-structure: \
                    it is locked if any cut already has grades registered (returns 409). \
                    Returns 404 if the subject does not exist.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subject updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or cut percentages do not sum to 100"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate subject name in the same semester, or evaluation structure is locked by existing grades")
    })
    @PutMapping("/{subjectId}")
    public ResponseEntity<com.aibert.dosw.entrypoints.ApiResponse<SubjectResponseDTO>> update(
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId,
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Valid @RequestBody SubjectRequestDTO request) {
        log.info("updateSubject - studentId={}, subjectId={}", studentId, subjectId);
        Subject subject = entrypointMapper.toDomain(request, studentId);
        Subject updated = updateSubjectUseCase.update(subjectId, subject);
        return ResponseEntity.ok(com.aibert.dosw.entrypoints.ApiResponse.ok(subjectMapper.toResponseDTO(updated)));
    }

    @Operation(
            summary = "Delete a subject",
            description = "Permanently deletes a subject along with all its evaluation cuts and grades (cascade). " +
                    "Returns 403 if the subject does not belong to the authenticated student.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Subject deleted successfully — no response body"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Subject does not belong to the authenticated student"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Parameter(description = "Numeric ID of the subject", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long subjectId) {
        log.info("deleteSubject - studentId={}, subjectId={}", studentId, subjectId);
        deleteSubjectUseCase.delete(subjectId, studentId);
        return ResponseEntity.noContent().build();
    }
}
