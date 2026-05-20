package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.StudyPreferencesRequestDTO;
import com.aibert.dosw.application.dto.response.StudyPreferencesResponseDTO;
import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.ports.in.GetStudyPreferencesUseCase;
import com.aibert.dosw.domain.ports.in.SaveStudyPreferencesUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Study Preferences", description = "Student study preferences configuration (AIB-12)")
@RestController
@RequestMapping("/api/v1/students/preferences")
@RequiredArgsConstructor
public class StudyPreferencesController {

    private final SaveStudyPreferencesUseCase saveStudyPreferencesUseCase;
    private final GetStudyPreferencesUseCase getStudyPreferencesUseCase;

    @PutMapping
    @Operation(summary = "Save or update study preferences")
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> save(
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody StudyPreferencesRequestDTO request) {

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
    @Operation(summary = "Get study preferences for the student")
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> get(
            @RequestHeader("X-Student-Id") String studentId) {

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
