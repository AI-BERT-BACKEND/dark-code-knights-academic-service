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
@DisplayName("Subject Integration Tests (R06)")
class SubjectIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubjectJpaRepository subjectJpaRepository;

    @Autowired
    private GradeJpaRepository gradeJpaRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Clean database before each test - delete in correct order to respect foreign key constraints
        gradeJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();
        
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should create subject successfully with valid data")
    void shouldCreateSubjectSuccessfullyWithValidData() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 60 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectName").value("Cálculo Diferencial"))
                .andExpect(jsonPath("$.data.credits").value(4))
                .andExpect(jsonPath("$.data.teacherName").value("Prof. Ramírez"))
                .andExpect(jsonPath("$.data.semester").value("2025-1"))
                .andExpect(jsonPath("$.data.evaluationCuts").isArray())
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(2)))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutName").value("Corte 1"))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutPercentage").value(40))
                .andExpect(jsonPath("$.data.evaluationCuts[1].cutName").value("Corte 2"))
                .andExpect(jsonPath("$.data.evaluationCuts[1].cutPercentage").value(60))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should return 400 when evaluation percentages don't sum 100")
    void shouldReturn400WhenEvaluationPercentagesDontSum100() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Materia Invalida",
              "credits": 3,
              "teacherName": "Prof. Error",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 40 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("La suma de porcentajes de los cortes debe ser exactamente 100")));
    }

    @Test
    @DisplayName("Should return 409 when creating duplicate subject")
    void shouldReturn409WhenCreatingDuplicateSubject() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 60 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("Ya existe una materia")));
    }

    @Test
    @DisplayName("Should list all subjects for student")
    void shouldListAllSubjectsForStudent() throws Exception {
        String subjectJson1 = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 60 }
              ]
            }
            """;

        String subjectJson2 = """
            {
              "subjectName": "Física General",
              "credits": 3,
              "teacherName": "Prof. Torres",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 50 },
                { "cutName": "Corte 2", "cutPercentage": 50 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson1));

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson2));

        mockMvc.perform(get("/api/v1/subjects")
                .header("X-Student-Id", "student-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].subjectName", containsInAnyOrder("Cálculo Diferencial", "Física General")));
    }

    @Test
    @DisplayName("Should get subject by ID successfully")
    void shouldGetSubjectByIdSuccessfully() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Cálculo Diferencial",
              "credits": 4,
              "teacherName": "Prof. Ramírez",
              "semester": "2025-1",
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
                .andReturn().getResponse().getContentAsString();

        Long subjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        mockMvc.perform(get("/api/v1/subjects/{id}", subjectId)
                .header("X-Student-Id", "student-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(subjectId))
                .andExpect(jsonPath("$.data.subjectName").value("Cálculo Diferencial"))
                .andExpect(jsonPath("$.data.credits").value(4))
                .andExpect(jsonPath("$.data.teacherName").value("Prof. Ramírez"))
                .andExpect(jsonPath("$.data.semester").value("2025-1"));
    }

    @Test
    @DisplayName("Should return 403 when getting subject not owned by student")
    void shouldReturn403WhenGettingNonExistentSubject() throws Exception {
        mockMvc.perform(get("/api/v1/subjects/{id}", 999)
                .header("X-Student-Id", "student-test"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("No tienes acceso a la materia con id 999")));
    }

    @Test
    @DisplayName("Should update subject successfully")
    void shouldUpdateSubjectSuccessfully() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Física General",
              "credits": 3,
              "teacherName": "Prof. Torres",
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
                .content(subjectJson))
                .andReturn().getResponse().getContentAsString();

        Long subjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        String updateJson = """
            {
              "subjectName": "Física Clásica",
              "credits": 4,
              "teacherName": "Prof. Torres",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 50 },
                { "cutName": "Corte 2", "cutPercentage": 50 }
              ]
            }
            """;

        mockMvc.perform(put("/api/v1/subjects/{id}", subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectName").value("Física Clásica"))
                .andExpect(jsonPath("$.data.credits").value(4))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should delete subject successfully")
    void shouldDeleteSubjectSuccessfully() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Física General",
              "credits": 3,
              "teacherName": "Prof. Torres",
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
                .content(subjectJson))
                .andReturn().getResponse().getContentAsString();

        Long subjectId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        mockMvc.perform(delete("/api/v1/subjects/{id}", subjectId)
                .header("X-Student-Id", "student-test"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 403 when deleting subject not owned by student")
    void shouldReturn403WhenDeletingNonExistentSubject() throws Exception {
        mockMvc.perform(delete("/api/v1/subjects/{id}", 999)
                .header("X-Student-Id", "student-test"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(containsString("No tienes acceso a la materia con id 999")));
    }

    @Test
    @DisplayName("Should return 400 when subject name is too short")
    void shouldReturn400WhenSubjectNameIsTooShort() throws Exception {
        String subjectJson = """
            {
              "subjectName": "AB",
              "credits": 3,
              "teacherName": "Prof. X",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when credits exceed maximum")
    void shouldReturn400WhenCreditsExceedMaximum() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Materia Inválida",
              "credits": 15,
              "teacherName": "Prof. X",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle subject with single evaluation cut")
    void shouldHandleSubjectWithSingleEvaluationCut() throws Exception {
        String subjectJson = """
            {
              "subjectName": "Materia Simple",
              "credits": 2,
              "teacherName": "Prof. Simple",
              "semester": "2025-1",
              "evaluationCuts": [
                { "cutName": "Corte Único", "cutPercentage": 100 }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Student-Id", "student-test")
                .content(subjectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.evaluationCuts").isArray())
                .andExpect(jsonPath("$.data.evaluationCuts", hasSize(1)))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutName").value("Corte Único"))
                .andExpect(jsonPath("$.data.evaluationCuts[0].cutPercentage").value(100));
    }
}
