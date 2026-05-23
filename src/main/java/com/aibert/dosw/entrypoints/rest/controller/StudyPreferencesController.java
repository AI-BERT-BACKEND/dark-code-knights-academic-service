package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.StudyPreferencesRequestDTO;
import com.aibert.dosw.application.dto.response.StudyPreferencesResponseDTO;
import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.ports.in.GetStudyPreferencesUseCase;
import com.aibert.dosw.domain.ports.in.SaveStudyPreferencesUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Study Preferences", description = "Manage the student's study preferences: modality, environment, and method. (AIB-12)")
@RestController
@RequestMapping("/api/v1/students/preferences")
@RequiredArgsConstructor
public class StudyPreferencesController {

    private final SaveStudyPreferencesUseCase saveStudyPreferencesUseCase;
    private final GetStudyPreferencesUseCase getStudyPreferencesUseCase;

    @PutMapping
    @Operation(
            summary = "Save or update study preferences",
            description = "Saves or replaces the study preferences for the authenticated student (upsert semantics). " +
                    "All fields are optional — send only the fields you want to store. " +
                    "A second PUT completely replaces the previous record.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Study preferences saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: a field exceeds its maximum length"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> save(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId,
            @Valid @RequestBody StudyPreferencesRequestDTO request) {

        log.info("saveStudyPreferences - studentId={}", studentId);
        StudyPreferences domain = StudyPreferences.builder()
                .studentId(studentId)
                .studyModality(request.getStudyModality())
                .studyEnvironment(request.getStudyEnvironment())
                .studyMethod(request.getStudyMethod())
                .build();

        StudyPreferences saved = saveStudyPreferencesUseCase.save(domain);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(saved), "Preferencias de estudio guardadas exitosamente"));
    }

    @GetMapping
    @Operation(
            summary = "Get study preferences for the student",
            description = "Returns the current study preferences of the authenticated student. " +
                    "All fields are nullable — they return null if no value has been configured yet.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Study preferences returned (fields may be null if not yet configured)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> get(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("studentId") String studentId) {

        log.info("getStudyPreferences - studentId={}", studentId);
        StudyPreferences preferences = getStudyPreferencesUseCase.get(studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(preferences)));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private StudyPreferencesResponseDTO toDTO(StudyPreferences p) {
        return StudyPreferencesResponseDTO.builder()
                .preferenceId(p.getId())
                .studyModality(p.getStudyModality())
                .studyEnvironment(p.getStudyEnvironment())
                .studyMethod(p.getStudyMethod())
                .build();
    }
}
