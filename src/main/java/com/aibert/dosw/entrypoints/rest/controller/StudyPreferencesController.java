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

@Tag(name = "Study Preferences", description = "Student study preference management (AIB-12)")
@RestController
@RequestMapping("/api/v1/students/preferences")
@RequiredArgsConstructor
public class StudyPreferencesController {

    private final SaveStudyPreferencesUseCase saveStudyPreferencesUseCase;
    private final GetStudyPreferencesUseCase getStudyPreferencesUseCase;

    @Operation(
            summary = "Save or update study preferences",
            description = "Creates or fully replaces the study preferences for the authenticated student. "
                    + "Valid values for preferredStudyTime: MORNING, AFTERNOON, EVENING, NIGHT. "
                    + "Valid values for preferredStudyMethod: INDIVIDUAL, GROUP, MIXED."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Preferences saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Validation error or invalid enum value")
    })
    @PutMapping
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> save(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody StudyPreferencesRequestDTO request) {

        StudyPreferences domain = StudyPreferences.builder()
                .studentId(studentId)
                .preferredStudyTime(request.getPreferredStudyTime())
                .preferredStudyMethod(request.getPreferredStudyMethod())
                .weeklyStudyHoursGoal(request.getWeeklyStudyHoursGoal())
                .preferredStudyLocation(request.getPreferredStudyLocation())
                .notificationsEnabled(request.isNotificationsEnabled())
                .build();

        StudyPreferences saved = saveStudyPreferencesUseCase.save(domain);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(saved), "Preferencias de estudio guardadas exitosamente"));
    }

    @Operation(
            summary = "Get study preferences",
            description = "Returns the study preferences previously saved for the authenticated student."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Preferences found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "No preferences found for this student")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<StudyPreferencesResponseDTO>> get(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId) {

        StudyPreferences preferences = getStudyPreferencesUseCase.get(studentId);
        return ResponseEntity.ok(ApiResponse.ok(toDTO(preferences)));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private StudyPreferencesResponseDTO toDTO(StudyPreferences p) {
        return StudyPreferencesResponseDTO.builder()
                .id(p.getId())
                .studentId(p.getStudentId())
                .preferredStudyTime(p.getPreferredStudyTime())
                .preferredStudyMethod(p.getPreferredStudyMethod())
                .weeklyStudyHoursGoal(p.getWeeklyStudyHoursGoal())
                .preferredStudyLocation(p.getPreferredStudyLocation())
                .notificationsEnabled(p.isNotificationsEnabled())
                .build();
    }
}
