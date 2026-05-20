package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.ScheduleAvailabilityRequestDTO;
import com.aibert.dosw.application.dto.response.ScheduleAvailabilityResponseDTO;
import com.aibert.dosw.application.mapper.ScheduleAvailabilityMapper;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.in.GetScheduleAvailabilityUseCase;
import com.aibert.dosw.domain.ports.in.SaveScheduleAvailabilityUseCase;
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

@RestController
@RequestMapping("/api/v1/students/schedule-availability")
@RequiredArgsConstructor
@Tag(name = "Schedule Availability", description = "Student schedule availability configuration (AIB-10)")
public class ScheduleAvailabilityController {

    private final SaveScheduleAvailabilityUseCase saveUseCase;
    private final GetScheduleAvailabilityUseCase getUseCase;
    private final ScheduleAvailabilityMapper mapper;

    @PutMapping
    @Operation(summary = "Save or update the student's schedule availability")
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponseDTO>> save(
            @RequestHeader("X-Student-Id") String studentId,
            @Valid @RequestBody ScheduleAvailabilityRequestDTO request) {

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
    @Operation(summary = "Get the student's schedule availability")
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponseDTO>> get(
            @RequestHeader("X-Student-Id") String studentId) {

        ScheduleAvailabilityResponseDTO response = mapper.toResponse(getUseCase.get(studentId));
        return ResponseEntity.ok(ApiResponse.ok(response, "ok"));
    }
}
