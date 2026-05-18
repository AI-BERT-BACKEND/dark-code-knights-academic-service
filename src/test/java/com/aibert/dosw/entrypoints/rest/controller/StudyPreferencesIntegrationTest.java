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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("Should create preferences with all fields")
    void shouldCreatePreferencesWithAllFields() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {
                                  "preferredStudyTime": "MORNING",
                                  "preferredStudyMethod": "INDIVIDUAL",
                                  "preferredStudyLocation": "Biblioteca Central"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Preferencias de estudio guardadas exitosamente"))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.data.preferredStudyTime").value("MORNING"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Biblioteca Central"))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    @DisplayName("Should create preferences with empty body (all fields optional)")
    void shouldCreatePreferencesWithEmptyBody() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should create preferences with only studyMethod")
    void shouldCreatePreferencesWithPartialFields() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyMethod": "GROUP"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("GROUP"));
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
                                    {"preferredStudyTime": "%s"}
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
                                    {"preferredStudyMethod": "%s"}
                                    """.formatted(method)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.preferredStudyMethod").value(method));
        }
    }

    // ─── PUT — update (upsert) ────────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences when called again")
    void shouldUpdateExistingPreferences() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyTime": "MORNING", "preferredStudyMethod": "INDIVIDUAL"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyTime": "EVENING", "preferredStudyMethod": "MIXED",
                                 "preferredStudyLocation": "Cafetería"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("EVENING"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("MIXED"))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Cafetería"));

        assertEquals(1L, studyPreferencesJpaRepository.count());
    }

    // ─── PUT — validation ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when preferredStudyLocation exceeds 100 characters")
    void shouldReturn400WhenLocationExceeds100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyLocation": "%s"}
                                """.formatted("A".repeat(101))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("100")));
    }

    @Test
    @DisplayName("Should return 400 when an invalid enum value is sent for preferredStudyTime")
    void shouldReturn400WhenInvalidStudyTimeEnum() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyTime": "INVALID_TIME"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when an invalid enum value is sent for preferredStudyMethod")
    void shouldReturn400WhenInvalidStudyMethodEnum() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyMethod": "SOLO"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing on PUT")
    void shouldReturn400WhenHeaderMissingOnPut() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return saved preferences on GET")
    void shouldReturnSavedPreferencesOnGet() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {
                                  "preferredStudyTime": "AFTERNOON",
                                  "preferredStudyMethod": "MIXED",
                                  "preferredStudyLocation": "Sala de estudio"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.preferredStudyTime").value("AFTERNOON"))
                .andExpect(jsonPath("$.data.preferredStudyMethod").value("MIXED"))
                .andExpect(jsonPath("$.data.preferredStudyLocation").value("Sala de estudio"))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID));
    }

    @Test
    @DisplayName("Should return 404 when no preferences exist for the student")
    void shouldReturn404WhenNoPreferencesExist() throws Exception {
        mockMvc.perform(get(URL)
                        .header("X-Student-Id", "unknown-student"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error", containsString("unknown-student")));
    }

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing on GET")
    void shouldReturn400WhenHeaderMissingOnGet() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isBadRequest());
    }

    // ─── Boundary values ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should accept preferredStudyLocation of exactly 100 characters")
    void shouldAcceptLocationOf100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"preferredStudyLocation": "%s"}
                                """.formatted("A".repeat(100))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyLocation", hasLength(100)));
    }

    // ─── Student isolation ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should isolate preferences per student")
    void shouldIsolatePreferencesPerStudent() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", "studentA")
                        .content("""
                                {"preferredStudyTime": "MORNING", "preferredStudyMethod": "INDIVIDUAL"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", "studentB")
                        .content("""
                                {"preferredStudyTime": "NIGHT", "preferredStudyMethod": "GROUP"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL).header("X-Student-Id", "studentA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("MORNING"));

        mockMvc.perform(get(URL).header("X-Student-Id", "studentB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.preferredStudyTime").value("NIGHT"));

        assertEquals(2L, studyPreferencesJpaRepository.count());
    }
}
