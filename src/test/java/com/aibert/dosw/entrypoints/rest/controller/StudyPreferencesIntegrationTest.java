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
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {
                                  "studyModality": "VISUAL",
                                  "studyEnvironment": "BIBLIOTECA",
                                  "studyMethod": "INDIVIDUAL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Preferencias de estudio guardadas exitosamente"))
                .andExpect(jsonPath("$.data.preferenceId").isNumber())
                .andExpect(jsonPath("$.data.studyModality").value("VISUAL"))
                .andExpect(jsonPath("$.data.studyEnvironment").value("BIBLIOTECA"))
                .andExpect(jsonPath("$.data.studyMethod").value("INDIVIDUAL"));
    }

    @Test
    @DisplayName("Should create preferences with empty body (all fields optional)")
    void shouldCreatePreferencesWithEmptyBody() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should create preferences with only studyModality")
    void shouldCreatePreferencesWithPartialFields() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"studyModality": "AUDITIVO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyModality").value("AUDITIVO"));
    }

    // ─── PUT — update (upsert) ────────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences when called again")
    void shouldUpdateExistingPreferences() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"studyModality": "VISUAL", "studyMethod": "INDIVIDUAL"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"studyModality": "AUDITIVO", "studyEnvironment": "CAFETERIA", "studyMethod": "GRUPO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyModality").value("AUDITIVO"))
                .andExpect(jsonPath("$.data.studyEnvironment").value("CAFETERIA"))
                .andExpect(jsonPath("$.data.studyMethod").value("GRUPO"));

        assertEquals(1L, studyPreferencesJpaRepository.count());
    }

    // ─── PUT — validation ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when studyMethod exceeds 100 characters")
    void shouldReturn400WhenStudyMethodExceeds100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"studyMethod": "%s"}
                                """.formatted("A".repeat(101))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("100")));
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
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {
                                  "studyModality": "KINESTESICO",
                                  "studyEnvironment": "SALA DE ESTUDIO",
                                  "studyMethod": "MIXTO"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL)
                        .header("studentId", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studyModality").value("KINESTESICO"))
                .andExpect(jsonPath("$.data.studyEnvironment").value("SALA DE ESTUDIO"))
                .andExpect(jsonPath("$.data.studyMethod").value("MIXTO"))
                .andExpect(jsonPath("$.data.preferenceId").isNumber());
    }

    @Test
    @DisplayName("Should return 404 when no preferences exist for the student")
    void shouldReturn404WhenNoPreferencesExist() throws Exception {
        mockMvc.perform(get(URL)
                        .header("studentId", "unknown-student"))
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
    @DisplayName("Should accept studyMethod of exactly 100 characters")
    void shouldAcceptStudyMethodOf100Characters() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"studyMethod": "%s"}
                                """.formatted("A".repeat(100))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyMethod", hasLength(100)));
    }

    // ─── Student isolation ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should isolate preferences per student")
    void shouldIsolatePreferencesPerStudent() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", "studentA")
                        .content("""
                                {"studyModality": "VISUAL", "studyMethod": "INDIVIDUAL"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", "studentB")
                        .content("""
                                {"studyModality": "AUDITIVO", "studyMethod": "GRUPO"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL).header("studentId", "studentA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyModality").value("VISUAL"));

        mockMvc.perform(get(URL).header("studentId", "studentB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studyModality").value("AUDITIVO"));

        assertEquals(2L, studyPreferencesJpaRepository.count());
    }
}
