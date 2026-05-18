package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.AcademicServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GradeJpaRepository;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SubjectJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AcademicServiceApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Schedule Availability Integration Tests (AIB-10)")
class ScheduleIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubjectJpaRepository subjectJpaRepository;

    @Autowired
    private GradeJpaRepository gradeJpaRepository;

    private MockMvc mockMvc;

    private static final String STUDENT_ID = "sched-student";
    private static final String SEMESTER = "2025-1";

    @BeforeEach
    void setUp() {
        gradeJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private void createSubject(String name, String schedule) throws Exception {
        String body = String.format("""
            {
              "subjectName": "%s",
              "credits": 3,
              "teacherName": "Prof. Test",
              "semester": "%s",
              "schedule": "%s",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """, name, SEMESTER, schedule);

        mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content(body))
                .andExpect(status().isCreated());
    }

    // ─── No conflicts ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return empty conflicts list when student has no subjects")
    void shouldReturnEmptyConflictsWhenNoSubjects() throws Exception {
        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.semester").value(SEMESTER))
                .andExpect(jsonPath("$.data.hasConflicts").value(false))
                .andExpect(jsonPath("$.data.conflicts").isArray())
                .andExpect(jsonPath("$.data.conflicts").isEmpty())
                .andExpect(jsonPath("$.message").value("No se detectaron conflictos de horario"));
    }

    @Test
    @DisplayName("Should return no conflicts when subjects are on different days")
    void shouldReturnNoConflictsForSubjectsOnDifferentDays() throws Exception {
        createSubject("Matemáticas", "LUNES 08:00-10:00");
        createSubject("Física", "MARTES 08:00-10:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(false))
                .andExpect(jsonPath("$.data.conflicts").isEmpty())
                .andExpect(jsonPath("$.message").value("No se detectaron conflictos de horario"));
    }

    @Test
    @DisplayName("Should return no conflicts when slots are on same day but only touch (non-overlapping)")
    void shouldReturnNoConflictsForTouchingSlots() throws Exception {
        createSubject("Matemáticas", "LUNES 08:00-10:00");
        createSubject("Física", "LUNES 10:00-12:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(false))
                .andExpect(jsonPath("$.data.conflicts").isEmpty());
    }

    @Test
    @DisplayName("Should return no conflicts when schedules are free-form (unparseable)")
    void shouldReturnNoConflictsForFreeFormSchedules() throws Exception {
        createSubject("Matemáticas", "Lunes 8 a 10");
        createSubject("Física", "Lunes 9 a 11");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(false))
                .andExpect(jsonPath("$.data.conflicts").isEmpty());
    }

    // ─── Conflicts detected ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should detect conflict when two subjects overlap on the same day")
    void shouldDetectConflictForOverlappingSubjects() throws Exception {
        createSubject("Matemáticas", "LUNES 08:00-10:00");
        createSubject("Física", "LUNES 09:00-11:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasConflicts").value(true))
                .andExpect(jsonPath("$.data.conflicts", hasSize(1)))
                .andExpect(jsonPath("$.data.conflicts[0].subjectAName").value("Matemáticas"))
                .andExpect(jsonPath("$.data.conflicts[0].subjectBName").value("Física"))
                .andExpect(jsonPath("$.data.conflicts[0].conflictingSlot").value("LUNES 08:00-10:00"))
                .andExpect(jsonPath("$.message").value("Se detectaron 1 conflicto(s) de horario"));
    }

    @Test
    @DisplayName("Should detect conflict when one slot is fully contained inside another")
    void shouldDetectConflictForContainedSlot() throws Exception {
        createSubject("Matemáticas", "JUEVES 08:00-12:00");
        createSubject("Física", "JUEVES 09:00-11:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(true))
                .andExpect(jsonPath("$.data.conflicts", hasSize(1)));
    }

    @Test
    @DisplayName("Should detect conflict only on the overlapping slot when subjects have multiple slots")
    void shouldDetectConflictOnlyOnOverlappingSlot() throws Exception {
        // A has LUNES + MIERCOLES; B conflicts only on MIERCOLES
        createSubject("Matemáticas", "LUNES 08:00-10:00, MIERCOLES 14:00-16:00");
        createSubject("Física", "MIERCOLES 15:00-17:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(true))
                .andExpect(jsonPath("$.data.conflicts", hasSize(1)))
                .andExpect(jsonPath("$.data.conflicts[0].conflictingSlot").value("MIERCOLES 14:00-16:00"));
    }

    @Test
    @DisplayName("Should detect multiple conflicts across three subjects")
    void shouldDetectMultipleConflictsAcrossThreeSubjects() throws Exception {
        createSubject("Algebra", "LUNES 08:00-10:00, MARTES 08:00-10:00");
        createSubject("Biologia", "LUNES 09:00-11:00");
        createSubject("Calculo", "MARTES 09:00-11:00");

        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(true))
                .andExpect(jsonPath("$.data.conflicts", hasSize(2)));
    }

    // ─── Semester isolation ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should not include subjects from a different semester in conflict analysis")
    void shouldNotIncludeSubjectsFromDifferentSemester() throws Exception {
        createSubject("Matemáticas", "LUNES 08:00-10:00");

        // Create a conflicting subject in a different semester directly
        String otherSemesterBody = """
            {
              "subjectName": "Física",
              "credits": 3,
              "teacherName": "Prof. Test",
              "semester": "2025-2",
              "schedule": "LUNES 09:00-11:00",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """;
        mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content(otherSemesterBody))
                .andExpect(status().isCreated());

        // Query for 2025-1 only → no conflict because Física is in 2025-2
        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasConflicts").value(false))
                .andExpect(jsonPath("$.data.conflicts").isEmpty());
    }

    // ─── Missing header ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing")
    void shouldReturn400WhenStudentIdHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .param("semester", SEMESTER))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when semester param is missing")
    void shouldReturn400WhenSemesterParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/academic/schedule-conflicts")
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isBadRequest());
    }
}
