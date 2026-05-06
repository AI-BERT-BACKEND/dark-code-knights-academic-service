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
@DisplayName("Simulation Integration Tests (R10)")
class SimulationIntegrationTest {

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
    private Long cut2Id;
    private Long cut3Id;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database before each test
        gradeJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String subjectJson = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 30 },
                { "cutName": "Corte 2", "cutPercentage": 30 },
                { "cutName": "Corte 3", "cutPercentage": 40 }
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
        cut2Id = objectMapper.readTree(createResponse).get("data").get("evaluationCuts").get(1).get("id").asLong();
        cut3Id = objectMapper.readTree(createResponse).get("data").get("evaluationCuts").get(2).get("id").asLong();

        setupGrades();
    }

    private void setupGrades() throws Exception {
        String gradeJson1 = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 5.0,
              "percentage": 100
            }
            """;

        String gradeJson2 = """
            {
              "activityName": "Paracial 2",
              "gradeValue": 3.5,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cut1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson1));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cut2Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson2));
    }

    @Test
    @DisplayName("Should simulate achievable target grade successfully")
    void shouldSimulateAchievableTargetGradeSuccessfully() throws Exception {
        String simulationJson = """
            {
              "targetGrade": 4.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(4.0))
                .andExpect(jsonPath("$.data.requiredGrade").value(3.625))
                .andExpect(jsonPath("$.data.achievable").value(true))
                .andExpect(jsonPath("$.data.pendingCutsPercentage").value(40.0))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should simulate unachievable target grade successfully")
    void shouldSimulateUnachievableTargetGradeSuccessfully() throws Exception {
        String simulationJson = """
            {
              "targetGrade": 5.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(5.0))
                .andExpect(jsonPath("$.data.requiredGrade").value(6.125))
                .andExpect(jsonPath("$.data.achievable").value(false))
                .andExpect(jsonPath("$.data.pendingCutsPercentage").value(40.0))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should simulate already achieved target grade successfully")
    void shouldSimulateAlreadyAchievedTargetGradeSuccessfully() throws Exception {
        String simulationJson = """
            {
              "targetGrade": 2.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(2.0))
                .andExpect(jsonPath("$.data.requiredGrade").value(0.0))
                .andExpect(jsonPath("$.data.achievable").value(true))
                .andExpect(jsonPath("$.data.pendingCutsPercentage").value(40.0))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should return 404 when subject does not exist")
    void shouldReturn404WhenSubjectDoesNotExist() throws Exception {
        String simulationJson = """
            {
              "targetGrade": 3.5
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("Materia con id 999 no encontrada")));
    }

    @Test
    @DisplayName("Should return 400 when target grade exceeds maximum")
    void shouldReturn400WhenTargetGradeExceedsMaximum() throws Exception {
        String simulationJson = """
            {
              "targetGrade": 6.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("La nota objetivo no puede superar 5.0")));
    }

    @Test
    @DisplayName("Should return 400 when target grade is negative")
    void shouldReturn400WhenTargetGradeIsNegative() throws Exception {
        String simulationJson = """
            {
              "targetGrade": -1.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when target grade is null")
    void shouldReturn400WhenTargetGradeIsNull() throws Exception {
        String simulationJson = """
            {
              "targetGrade": null
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("La nota objetivo no puede ser nula")));
    }

    @Test
    @DisplayName("Should return 422 when all cuts have grades")
    void shouldReturn422WhenAllCutsHaveGrades() throws Exception {
        String gradeJson3 = """
            {
              "activityName": "Examen Final",
              "gradeValue": 3.8,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cut3Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson3));

        String simulationJson = """
            {
              "targetGrade": 3.5
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("no tiene cortes pendientes por calificar")));
    }

    @Test
    @DisplayName("Should handle subject with no graded cuts")
    void shouldHandleSubjectWithNoGradedCuts() throws Exception {
        String newSubjectJson = """
            {
              "subjectName": "Álgebra Lineal",
              "credits": 3,
              "teacherName": "Prof. García",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 50 },
                { "cutName": "Corte 2", "cutPercentage": 50 }
              ]
            }
            """;

        String createResponse = mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(newSubjectJson))
                .andReturn().getResponse().getContentAsString();

        Long newSubjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        String simulationJson = """
            {
              "targetGrade": 3.5
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", newSubjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(3.5))
                .andExpect(jsonPath("$.data.requiredGrade").value(3.5))
                .andExpect(jsonPath("$.data.achievable").value(true))
                .andExpect(jsonPath("$.data.pendingCutsPercentage").value(100.0));
    }

    @Test
    @DisplayName("Should handle subject with single pending cut")
    void shouldHandleSubjectWithSinglePendingCut() throws Exception {
        String singleCutSubjectJson = """
            {
              "subjectName": "Materia Simple",
              "credits": 2,
              "teacherName": "Prof. Simple",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 60 },
                { "cutName": "Corte 2", "cutPercentage": 40 }
              ]
            }
            """;

        String createResponse = mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(singleCutSubjectJson))
                .andReturn().getResponse().getContentAsString();

        Long singleCutSubjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();
        Long singleCutCut1Id = objectMapper.readTree(createResponse).get("data").get("evaluationCuts").get(0).get("id").asLong();

        String gradeJson = """
            {
              "activityName": "Parcial Único",
              "gradeValue": 4.0,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", singleCutSubjectId, singleCutCut1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson));

        String simulationJson = """
            {
              "targetGrade": 3.5
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", singleCutSubjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(simulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(3.5))
                .andExpect(jsonPath("$.data.requiredGrade").value(2.75))
                .andExpect(jsonPath("$.data.achievable").value(true))
                .andExpect(jsonPath("$.data.pendingCutsPercentage").value(40.0));
    }

    @Test
    @DisplayName("Should handle boundary target grades")
    void shouldHandleBoundaryTargetGrades() throws Exception {
        String zeroSimulationJson = """
            {
              "targetGrade": 0.0
            }
            """;

        String perfectSimulationJson = """
            {
              "targetGrade": 5.0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(zeroSimulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(0.0))
                .andExpect(jsonPath("$.data.requiredGrade").value(0.0))
                .andExpect(jsonPath("$.data.achievable").value(true));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/simulate", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(perfectSimulationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(5.0))
                .andExpect(jsonPath("$.data.requiredGrade").value(6.125))
                .andExpect(jsonPath("$.data.achievable").value(false));
    }

    @Test
    @DisplayName("Should verify final averages after all cuts are graded")
    void shouldVerifyFinalAveragesAfterAllCutsAreGraded() throws Exception {
        String gradeJson3 = """
            {
              "activityName": "Examen Final",
              "gradeValue": 3.8,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cut3Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson3));

        mockMvc.perform(get("/api/v1/subjects/{subjectId}/averages", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cuts").isArray())
                .andExpect(jsonPath("$.data.cuts", hasSize(3)))
                .andExpect(jsonPath("$.data.cuts[0].grade").value(5.0))
                .andExpect(jsonPath("$.data.cuts[1].grade").value(3.5))
                .andExpect(jsonPath("$.data.cuts[2].grade").value(3.8))
                .andExpect(jsonPath("$.data.overallAverage").value(4.07));
    }
}
