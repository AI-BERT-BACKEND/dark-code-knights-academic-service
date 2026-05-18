package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.AcademicServiceApplication;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.StudyPreferencesJpaRepository;
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
@DisplayName("Study Preferences Integration Tests (AIB-12)")
class StudyPreferencesIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudyPreferencesJpaRepository studyPreferencesJpaRepository;

    private MockMvc mockMvc;

    private static final String STUDENT_ID = "pref-student";
    private static final String URL = "/api/v1/students/preferences";

    @BeforeEach
    void setUp() {
        studyPreferencesJpaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ─── PUT — create ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create preferences successfully with all valid fields")
    void shouldCreatePreferencesSuccessfully() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Biblioteca Central",
                              "notificationsEnabled": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Preferencias de estudio guardadas exitosamente"))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.data.preferredStudyTime").value("MORNING"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(10))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Biblioteca Central"))
                .andExpect(jsonPath("$.data.notificationsEnabled").value(true))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("Should create preferences with notificationsEnabled=false")
    void shouldCreatePreferencesWithNotificationsDisabled() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "NIGHT",
                              "preferredStudyMethod": "GROUP",
                              "weeklyStudyHoursGoal": 5,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notificationsEnabled").value(false));
    }

    @Test
    @DisplayName("Should accept all valid StudyTime values")
    void shouldAcceptAllStudyTimeValues() throws Exception {
        for (String time : new String[]{"MORNING", "AFTERNOON", "EVENING", "NIGHT"}) {
            studyPreferencesJpaRepository.deleteAll();
            mockMvc.perform(put(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Student-Id", STUDENT_ID)
                            .content("""
                                {
                                  "preferredStudyTime": "%s",
                                  "preferredStudyMethod": "INDIVIDUAL",
                                  "weeklyStudyHoursGoal": 8,
                                  "preferredStudyLocation": "Casa",
                                  "notificationsEnabled": false
                                }
                                """.formatted(time)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.preferredStudyTime").value(time));
        }
    }

    @Test
    @DisplayName("Should accept all valid StudyMethod values")
    void shouldAcceptAllStudyMethodValues() throws Exception {
        for (String method : new String[]{"INDIVIDUAL", "GROUP", "MIXED"}) {
            studyPreferencesJpaRepository.deleteAll();
            mockMvc.perform(put(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Student-Id", STUDENT_ID)
                            .content("""
                                {
                                  "preferredStudyTime": "MORNING",
                                  "preferredStudyMethod": "%s",
                                  "weeklyStudyHoursGoal": 8,
                                  "preferredStudyLocation": "Casa",
                                  "notificationsEnabled": false
                                }
                                """.formatted(method)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.preferredStudyMethod").value(method));
        }
    }

    // ─── PUT — update ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences when called again")
    void shouldUpdateExistingPreferences() throws Exception {
        // First save
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 5,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isOk());

        // Update
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "EVENING",
                              "preferredStudyMethod": "MIXED",
                              "weeklyStudyHoursGoal": 20,
                              "preferredStudyLocation": "Cafetería",
                              "notificationsEnabled": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("EVENING"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("MIXED"))
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(20))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Cafetería"))
                .andExpect(jsonPath("$.data.notificationsEnabled").value(true));

        // Only one DB record should exist
        org.junit.jupiter.api.Assertions.assertEquals(1, studyPreferencesJpaRepository.count());
    }

    // ─── PUT — validation errors ──────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when preferredStudyTime is missing")
    void shouldReturn400WhenStudyTimeIsMissing() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("horario preferido")));
    }

    @Test
    @DisplayName("Should return 400 when preferredStudyMethod is missing")
    void shouldReturn400WhenStudyMethodIsMissing() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("método de estudio")));
    }

    @Test
    @DisplayName("Should return 400 when weeklyStudyHoursGoal is zero")
    void shouldReturn400WhenWeeklyHoursIsZero() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 0,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("al menos 1")));
    }

    @Test
    @DisplayName("Should return 400 when weeklyStudyHoursGoal exceeds 168")
    void shouldReturn400WhenWeeklyHoursExceeds168() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 169,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("168")));
    }

    @Test
    @DisplayName("Should return 400 when preferredStudyLocation is blank")
    void shouldReturn400WhenLocationIsBlank() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("lugar de estudio")));
    }

    @Test
    @DisplayName("Should return 400 when preferredStudyLocation exceeds 100 characters")
    void shouldReturn400WhenLocationExceeds100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "%s",
                              "notificationsEnabled": false
                            }
                            """.formatted("A".repeat(101))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("100")));
    }

    @Test
    @DisplayName("Should return 400 when an invalid enum value is sent")
    void shouldReturn400WhenInvalidEnumValue() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "INVALID_VALUE",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing")
    void shouldReturn400WhenStudentIdHeaderMissing() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return saved preferences on GET")
    void shouldReturnSavedPreferencesOnGet() throws Exception {
        // Save first
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "AFTERNOON",
                              "preferredStudyMethod": "MIXED",
                              "weeklyStudyHoursGoal": 12,
                              "preferredStudyLocation": "Sala de estudio",
                              "notificationsEnabled": true
                            }
                            """))
                .andExpect(status().isOk());

        // Then GET
        mockMvc.perform(get(URL)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.preferredStudyTime").value("AFTERNOON"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("MIXED"))
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(12))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Sala de estudio"))
                .andExpect(jsonPath("$.data.notificationsEnabled").value(true))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID));
    }

    @Test
    @DisplayName("Should return 404 when no preferences exist for the student")
    void shouldReturn404WhenNoPreferencesExist() throws Exception {
        mockMvc.perform(get(URL)
                        .header("X-Student-Id", "unknown-student"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("unknown-student")));
    }

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing on GET")
    void shouldReturn400WhenStudentIdHeaderMissingOnGet() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isBadRequest());
    }

    // ─── Boundary values ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should accept weeklyStudyHoursGoal of 1 (minimum)")
    void shouldAcceptMinimumWeeklyHours() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 1,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(1));
    }

    @Test
    @DisplayName("Should accept weeklyStudyHoursGoal of 168 (maximum)")
    void shouldAcceptMaximumWeeklyHours() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 168,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(168));
    }

    @Test
    @DisplayName("Should accept preferredStudyLocation of exactly 100 characters")
    void shouldAcceptLocationOf100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "%s",
                              "notificationsEnabled": false
                            }
                            """.formatted("A".repeat(100))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyLocation", hasLength(100)));
    }

    // ─── Student isolation ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should isolate preferences per student — each student has their own record")
    void shouldIsolatePreferencesPerStudent() throws Exception {
        // Student A saves preferences
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", "studentA")
                        .content("""
                            {
                              "preferredStudyTime": "MORNING",
                              "preferredStudyMethod": "INDIVIDUAL",
                              "weeklyStudyHoursGoal": 10,
                              "preferredStudyLocation": "Biblioteca",
                              "notificationsEnabled": true
                            }
                            """))
                .andExpect(status().isOk());

        // Student B saves different preferences
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", "studentB")
                        .content("""
                            {
                              "preferredStudyTime": "NIGHT",
                              "preferredStudyMethod": "GROUP",
                              "weeklyStudyHoursGoal": 20,
                              "preferredStudyLocation": "Casa",
                              "notificationsEnabled": false
                            }
                            """))
                .andExpect(status().isOk());

        // Each student gets their own preferences
        mockMvc.perform(get(URL).header("X-Student-Id", "studentA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("MORNING"))
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(10));

        mockMvc.perform(get(URL).header("X-Student-Id", "studentB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("NIGHT"))
                .andExpect(jsonPath("$.data.weeklyStudyHoursGoal").value(20));

        org.junit.jupiter.api.Assertions.assertEquals(2, studyPreferencesJpaRepository.count());
    }
}
