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
@DisplayName("Grade Integration Tests (R08)")
class GradeIntegrationTest {

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
    private Long cutId;

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
        cutId = objectMapper.readTree(createResponse).get("data").get("evaluationCuts").get(0).get("id").asLong();
    }

    @Test
    @DisplayName("Should register grade successfully with valid data")
    void shouldRegisterGradeSuccessfullyWithValidData() throws Exception {
        String gradeJson = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activityName").value("Parcial 1"))
                .andExpect(jsonPath("$.data.gradeValue").value(4.5))
                .andExpect(jsonPath("$.data.percentage").value(60))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should return 400 when grade exceeds maximum")
    void shouldReturn400WhenGradeExceedsMaximum() throws Exception {
        String gradeJson = """
            {
              "activityName": "Paracial Invalido",
              "gradeValue": 6.0,
              "percentage": 50
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("La nota máxima es 5.0")));
    }

    @Test
    @DisplayName("Should return 400 when grade is negative")
    void shouldReturn400WhenGradeIsNegative() throws Exception {
        String gradeJson = """
            {
              "activityName": "Paracial Invalido",
              "gradeValue": -1.0,
              "percentage": 50
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when percentage is zero")
    void shouldReturn400WhenPercentageIsZero() throws Exception {
        String gradeJson = """
            {
              "activityName": "Paracial Invalido",
              "gradeValue": 4.0,
              "percentage": 0
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 404 when subject does not exist")
    void shouldReturn404WhenSubjectDoesNotExist() throws Exception {
        String gradeJson = """
            {
              "activityName": "Parcial",
              "gradeValue": 4.0,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", 999, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("Materia con id 999 no encontrada")));
    }

    @Test
    @DisplayName("Should return 404 when cut does not exist")
    void shouldReturn404WhenCutDoesNotExist() throws Exception {
        String gradeJson = """
            {
              "activityName": "Parcial",
              "gradeValue": 4.0,
              "percentage": 100
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("Materia con id")));
    }

    @Test
    @DisplayName("Should register multiple grades in same cut successfully")
    void shouldRegisterMultipleGradesInSameCutSuccessfully() throws Exception {
        String gradeJson1 = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String gradeJson2 = """
            {
              "activityName": "Quiz 1",
              "gradeValue": 3.0,
              "percentage": 40
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activityName").value("Quiz 1"))
                .andExpect(jsonPath("$.data.gradeValue").value(3.0))
                .andExpect(jsonPath("$.data.percentage").value(40));
    }

    @Test
    @DisplayName("Should return 422 when cut capacity is exceeded")
    void shouldReturn422WhenCutCapacityIsExceeded() throws Exception {
        String gradeJson1 = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String gradeJson2 = """
            {
              "activityName": "Quiz 1",
              "gradeValue": 3.0,
              "percentage": 40
            }
            """;

        String gradeJson3 = """
            {
              "activityName": "Taller Extra",
              "gradeValue": 4.0,
              "percentage": 10
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson1));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson2));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson3))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("No es posible agregar la actividad")));
    }

    @Test
    @DisplayName("Should list grades by cut successfully")
    void shouldListGradesByCutSuccessfully() throws Exception {
        String gradeJson1 = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String gradeJson2 = """
            {
              "activityName": "Quiz 1",
              "gradeValue": 3.0,
              "percentage": 40
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson1));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson2));

        mockMvc.perform(get("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].activityName", containsInAnyOrder("Parcial 1", "Quiz 1")))
                .andExpect(jsonPath("$.data[*].gradeValue", containsInAnyOrder(4.5, 3.0)))
                .andExpect(jsonPath("$.data[*].percentage", containsInAnyOrder(60.0, 40.0)));
    }

    @Test
    @DisplayName("Should return empty list when cut has no grades")
    void shouldReturnEmptyListWhenCutHasNoGrades() throws Exception {
        mockMvc.perform(get("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should update grade successfully")
    void shouldUpdateGradeSuccessfully() throws Exception {
        String gradeJson = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String createResponse = mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andReturn().getResponse().getContentAsString();

        Long gradeId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        String updateJson = """
            {
              "activityName": "Parcial 1 Actualizado",
              "gradeValue": 4.8,
              "percentage": 60
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}", subjectId, cutId, gradeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activityName").value("Parcial 1 Actualizado"))
                .andExpect(jsonPath("$.data.gradeValue").value(4.8))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should delete grade successfully")
    void shouldDeleteGradeSuccessfully() throws Exception {
        String gradeJson = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String createResponse = mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
                .andReturn().getResponse().getContentAsString();

        Long gradeId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        mockMvc.perform(delete("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades/{gradeId}", subjectId, cutId, gradeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Nota eliminada exitosamente"));
    }

    @Test
    @DisplayName("Should get averages with calculated cut grades")
    void shouldGetAveragesWithCalculatedCutGrades() throws Exception {
        String gradeJson1 = """
            {
              "activityName": "Parcial 1",
              "gradeValue": 4.5,
              "percentage": 60
            }
            """;

        String gradeJson2 = """
            {
              "activityName": "Quiz 1",
              "gradeValue": 3.0,
              "percentage": 40
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson1));

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson2));

        mockMvc.perform(get("/api/v1/subjects/{subjectId}/averages", subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cuts").isArray())
                .andExpect(jsonPath("$.data.cuts", hasSize(3)))
                .andExpect(jsonPath("$.data.cuts[0].grade").value(3.9))
                .andExpect(jsonPath("$.data.cuts[1].grade").doesNotExist())
                .andExpect(jsonPath("$.data.cuts[2].grade").doesNotExist())
                .andExpect(jsonPath("$.data.overallAverage").value(1.17));
    }

    @Test
    @DisplayName("Should handle boundary grade values")
    void shouldHandleBoundaryGradeValues() throws Exception {
        String minGradeJson = """
            {
              "activityName": "Taller Mínimo",
              "gradeValue": 0.0,
              "percentage": 50
            }
            """;

        String maxGradeJson = """
            {
              "activityName": "Examen Máximo",
              "gradeValue": 5.0,
              "percentage": 50
            }
            """;

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(minGradeJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/subjects/{subjectId}/cuts/{cutId}/grades", subjectId, cutId + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(maxGradeJson))
                .andExpect(status().isCreated());
    }
}
