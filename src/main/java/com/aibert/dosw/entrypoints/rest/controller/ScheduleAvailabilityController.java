package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.ScheduleAvailabilityRequestDTO;
import com.aibert.dosw.application.dto.response.ScheduleAvailabilityResponseDTO;
import com.aibert.dosw.application.mapper.ScheduleAvailabilityMapper;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.in.GetScheduleAvailabilityUseCase;
import com.aibert.dosw.domain.ports.in.SaveScheduleAvailabilityUseCase;
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
@Tag(name = "Schedule Availability", description = "Manage the student's daily schedule availability: free time, rest, personal and social hours. (AIB-10)")
@RestController
@RequestMapping("/api/v1/students/schedule-availability")
@RequiredArgsConstructor
public class ScheduleAvailabilityController {

    private final SaveScheduleAvailabilityUseCase saveUseCase;
    private final GetScheduleAvailabilityUseCase getUseCase;
    private final ScheduleAvailabilityMapper mapper;

    @PutMapping
    @Operation(
            summary = "Save or update schedule availability",
            description = """
                    Saves or replaces the daily schedule availability for the authenticated student (upsert semantics). \
                    All hour fields are optional and must be greater than 0.0 if provided. \
                    maxStudyHoursPerDay must be ≤ 15. \
                    The sum of freeTimeHours + restHours + personalTimeHours + socialTimeHours must not exceed 24.""",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schedule availability saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error: a field is ≤ 0, maxStudyHoursPerDay > 15, or total hours exceed 24"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponseDTO>> save(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody ScheduleAvailabilityRequestDTO request) {

        log.info("saveScheduleAvailability - studentId={}", studentId);
        ScheduleAvailability domain = ScheduleAvailability.builder()
                .studentId(studentId)
                .freeTimeHours(request.getFreeTimeHours())
                .restHours(request.getRestHours())
                .personalTimeHours(request.getPersonalTimeHours())
                .socialTimeHours(request.getSocialTimeHours())
                .maxStudyHoursPerDay(request.getMaxStudyHoursPerDay())
                .build();

        ScheduleAvailabilityResponseDTO response = mapper.toResponse(saveUseCase.save(domain));
        return ResponseEntity.ok(ApiResponse.ok(response, "Disponibilidad de horarios guardada exitosamente"));
    }

    @GetMapping
    @Operation(
            summary = "Get schedule availability",
            description = "Returns the current daily schedule availability of the authenticated student. " +
                    "All hour fields are nullable — they return null if no value has been configured yet.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schedule availability returned (hour fields may be null if not yet configured)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponseDTO>> get(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId) {

        log.info("getScheduleAvailability - studentId={}", studentId);
        ScheduleAvailabilityResponseDTO response = mapper.toResponse(getUseCase.get(studentId));
        return ResponseEntity.ok(ApiResponse.ok(response, "ok"));
    }
}
