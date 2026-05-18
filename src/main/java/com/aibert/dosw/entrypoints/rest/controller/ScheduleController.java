package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.ScheduleAvailabilityResponseDTO;
import com.aibert.dosw.application.dto.response.ScheduleConflictDTO;
import com.aibert.dosw.domain.model.ScheduleConflict;
import com.aibert.dosw.domain.ports.in.DetectScheduleConflictsUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Schedule Availability", description = "Detect pairwise schedule conflicts between a student's subjects (AIB-10)")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final DetectScheduleConflictsUseCase detectScheduleConflictsUseCase;

    @Operation(
            summary = "Detect schedule conflicts",
            description = "Returns all pairwise schedule conflicts between the student's subjects for the given semester. "
                    + "Schedules must follow the format 'DÍA HH:MM-HH:MM' (e.g. 'LUNES 08:00-10:00'). "
                    + "An empty conflicts list means no overlaps were found."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Conflict analysis completed (no conflicts → empty list)")
    })
    @GetMapping("/api/v1/academic/schedule-conflicts")
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponseDTO>> getConflicts(
            @Parameter(description = "Authenticated student ID", required = true)
            @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "Semester to analyse (e.g. 2025-1)", required = true)
            @RequestParam String semester) {

        List<ScheduleConflict> conflicts = detectScheduleConflictsUseCase.detect(studentId, semester);

        List<ScheduleConflictDTO> conflictDTOs = conflicts.stream()
                .map(c -> ScheduleConflictDTO.builder()
                        .subjectAId(c.getSubjectAId())
                        .subjectAName(c.getSubjectAName())
                        .subjectBId(c.getSubjectBId())
                        .subjectBName(c.getSubjectBName())
                        .conflictingSlot(c.getConflictingSlot())
                        .build())
                .toList();

        ScheduleAvailabilityResponseDTO response = ScheduleAvailabilityResponseDTO.builder()
                .semester(semester)
                .hasConflicts(!conflictDTOs.isEmpty())
                .conflicts(conflictDTOs)
                .build();

        String message = conflictDTOs.isEmpty()
                ? "No se detectaron conflictos de horario"
                : "Se detectaron " + conflictDTOs.size() + " conflicto(s) de horario";

        return ResponseEntity.ok(ApiResponse.ok(response, message));
    }
}
