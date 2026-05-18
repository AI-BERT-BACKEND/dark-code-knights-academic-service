package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.AcademicServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SubjectJpaRepository;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GradeJpaRepository;
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
@DisplayName("Evaluation Structure Integration Tests (R07)")
class EvaluationStructureIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubjectJpaRepository subjectJpaRepository;

    @Autowired
    private GradeJpaRepository gradeJpaRepository;

    private MockMvc mockMvc;
    private Long subjectId;
    private Long cut1Id;

    @BeforeEach
    void setUp() throws Exception {
        gradeJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String subjectJson = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
              "schedule": "Lunes 08:30 - 10:00",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 60 }
              ]
            }
            """;

        String createResponse = mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        subjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();
        cut1Id = objectMapper.readTree(createResponse).get("data").get("evaluationCuts").get(0).get("id").asLong();
    }

    // ───────── GET /evaluation-structure ─────────

    @Test
    @DisplayName("Should get evaluation structure successfully")
    void shouldGetEvaluationStructureSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectId").value(subjectId))
                .andExpect(jsonPath("$.data.evaluationCuts").isArray())
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(2)))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutName").value("Corte 1"))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutPercentage").value(40.0))
                .andExpect(jsonPath("$.data.evaluationCuts[1].cutName").value("Corte 2"))
                .andExpect(jsonPath("$.data.evaluationCuts[1].cutPercentage").value(60.0))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should return 404 when getting structure of non-existent subject")
    void shouldReturn404WhenGettingStructureOfNonExistentSubject() throws Exception {
        mockMvc.perform(get("/api/v1/subjects/{subjectId}/evaluation-structure", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("999")));
    }

    // ───────── PUT /evaluation-structure ─────────

    @Test
    @DisplayName("Should configure evaluation structure successfully")
    void shouldConfigureEvaluationStructureSuccessfully() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 30 },
                { "cutName": "Corte 2", "cutPercentage": 30 },
                { "cutName": "Corte 3", "cutPercentage": 40 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectId").value(subjectId))
                .andExpect(jsonPath("$.data.evaluationCuts").isArray())
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(3)))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutName").value("Corte 1"))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutPercentage").value(30.0))
                .andExpect(jsonPath("$.data.evaluationCuts[2].cutName").value("Corte 3"))
                .andExpect(jsonPath("$.data.evaluationCuts[2].cutPercentage").value(40.0))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should return 400 when evaluation percentages do not sum to 100")
    void shouldReturn400WhenPercentagesDoNotSumTo100() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 40 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("100")));
    }

    @Test
    @DisplayName("Should return 400 when evaluation cuts list is empty")
    void shouldReturn400WhenEvaluationCutsListIsEmpty() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": []
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 404 when configuring structure of non-existent subject")
    void shouldReturn404WhenConfiguringStructureOfNonExistentSubject() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("999")));
    }

    @Test
    @DisplayName("Should return 409 when structure is locked because grades are registered")
    void shouldReturn409WhenStructureIsLockedBecauseGradesAreRegistered() throws Exception {
        // Register a grade in the first cut to lock the structure
        String gradeJson = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.0,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cut1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isCreated());

        // Now try to reconfigure
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Nuevo Corte", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("no puede editarse porque ya tiene notas registradas")));
    }

    @Test
    @DisplayName("Should reconfigure with single cut successfully")
    void shouldReconfigureWithSingleCutSuccessfully() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Corte Único", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(1)))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutName").value("Corte Único"))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutPercentage").value(100.0));
    }

    @Test
    @DisplayName("Should reflect updated structure when GET is called after PUT")
    void shouldReflectUpdatedStructureAfterConfigure() throws Exception {
        String structureJson = """
            {
              "evaluationCuts": [
                { "cutName": "Nuevo Corte 1", "cutPercentage": 50 },
                { "cutName": "Nuevo Corte 2", "cutPercentage": 50 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(structureJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/subjects/{subjectId}/evaluation-structure", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(2)))
                .andExpect(jsonPath("$.data.evaluationCuts[*].cutName",
                        containsInAnyOrder("Nuevo Corte 1", "Nuevo Corte 2")));
    }
}
