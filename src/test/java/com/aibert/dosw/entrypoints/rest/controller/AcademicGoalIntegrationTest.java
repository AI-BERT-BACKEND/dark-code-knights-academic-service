package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.AcademicServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.AcademicGoalJpaRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AcademicServiceApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Academic Goals Integration Tests (AIB-11)")
class AcademicGoalIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubjectJpaRepository subjectJpaRepository;

    @Autowired
    private GradeJpaRepository gradeJpaRepository;

    @Autowired
    private AcademicGoalJpaRepository goalJpaRepository;

    private MockMvc mockMvc;

    private static final String STUDENT_ID = "goal-student";
    private static final String SEMESTER = "2025-1";
    private static final String GOALS_URL = "/api/v1/academic/goals";

    private Long subjectId;
    private Long cut1Id;
    private Long cut2Id;

    @BeforeEach
    void setUp() throws Exception {
        gradeJpaRepository.deleteAll();
        goalJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String response = mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {
                                  "subjectName": "Cálculo Integral",
                                  "credits": 4,
                                  "teacherName": "Prof. García",
                                  "semester": "2025-1",
                                  "schedule": "LUNES 08:00-10:00",
                                  "evaluationCuts": [
                                    {"cutName": "Corte 1", "cutPercentage": 40},
                                    {"cutName": "Corte 2", "cutPercentage": 60}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        subjectId = objectMapper.readTree(response).get("data").get("id").asLong();
        cut1Id = objectMapper.readTree(response).get("data").get("evaluationCuts").get(0).get("id").asLong();
        cut2Id = objectMapper.readTree(response).get("data").get("evaluationCuts").get(1).get("id").asLong();
    }

    // ─── PUT — general goal (no subject) ─────────────────────────────────────

    @Test
    @DisplayName("Should create a general goal without subjectId")
    void shouldCreateGeneralGoal() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Mejorar mi promedio general", "targetGrade": 4.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("¡Meta guardada exitosamente!"))
                .andExpect(jsonPath("$.data.goalId").isNumber())
                .andExpect(jsonPath("$.data.goalName").value("Mejorar mi promedio general"))
                .andExpect(jsonPath("$.data.targetGrade").value(4.0))
                .andExpect(jsonPath("$.data.subjectId").doesNotExist());
    }

    // ─── PUT — subject-linked goal ────────────────────────────────────────────

    @Test
    @DisplayName("Should create a subject-linked goal")
    void shouldCreateSubjectLinkedGoal() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 4.0, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.goalName").value("Aprobar Cálculo"))
                .andExpect(jsonPath("$.data.subjectId").value(subjectId.intValue()))
                .andExpect(jsonPath("$.data.requiredGrade").value(4.0))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    @Test
    @DisplayName("Should update existing goal (same goalName) on second PUT")
    void shouldUpdateExistingGoal() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 3.0}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 4.5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.targetGrade").value(4.5));

        assertEquals(1L, goalJpaRepository.count());
    }

    @Test
    @DisplayName("Should create separate records for different goal names")
    void shouldCreateSeparateGoalsForDifferentNames() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta A", "targetGrade": 3.5}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta B", "targetGrade": 4.0}
                                """))
                .andExpect(status().isOk());

        assertEquals(2L, goalJpaRepository.count());
    }

    // ─── PUT — validation ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when goalName is missing")
    void shouldReturn400WhenGoalNameMissing() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"targetGrade": 4.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("meta")));
    }

    @Test
    @DisplayName("Should return 400 when goalName is too short (< 3 chars)")
    void shouldReturn400WhenGoalNameTooShort() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "AB", "targetGrade": 4.0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when targetGrade is null")
    void shouldReturn400WhenTargetGradeNull() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Mi meta"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when targetGrade exceeds 5.0")
    void shouldReturn400WhenTargetGradeExceedsFive() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Mi meta", "targetGrade": 5.1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when subjectId does not belong to student")
    void shouldReturn404WhenSubjectNotOwned() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", "other-student")
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 4.0, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isNotFound());
    }

    // ─── GET /{goalId} ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return goal by id after creating it")
    void shouldReturnGoalById() throws Exception {
        String createResponse = mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Sacar buen promedio", "targetGrade": 3.5}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long goalId = objectMapper.readTree(createResponse).get("data").get("goalId").asLong();

        mockMvc.perform(get(GOALS_URL + "/{goalId}", goalId)
                        .header("studentId", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.goalName").value("Sacar buen promedio"))
                .andExpect(jsonPath("$.data.targetGrade").value(3.5));
    }

    @Test
    @DisplayName("Should return 404 when goal does not exist")
    void shouldReturn404WhenGoalNotFound() throws Exception {
        mockMvc.perform(get(GOALS_URL + "/99999")
                        .header("studentId", STUDENT_ID))
                .andExpect(status().isNotFound());
    }

    // ─── GET (list) ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all goals when semester is not specified")
    void shouldReturnAllGoalsWithoutSemesterFilter() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta A", "targetGrade": 3.5}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta B", "targetGrade": 4.0, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isOk());

        mockMvc.perform(get(GOALS_URL)
                        .header("studentId", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @DisplayName("Should return empty list when student has no goals")
    void shouldReturnEmptyListWhenNoGoals() throws Exception {
        mockMvc.perform(get(GOALS_URL)
                        .header("studentId", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should filter goals by semester — exclude general goals")
    void shouldFilterGoalsBySemester() throws Exception {
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 4.0, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isOk());

        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta general", "targetGrade": 3.5}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(GOALS_URL)
                        .header("studentId", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    // ─── Progress after partial grading ──────────────────────────────────────

    @Test
    @DisplayName("Should compute requiredGrade correctly after one cut is graded")
    void shouldComputeRequiredGradeAfterPartialGrading() throws Exception {
        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"activityName": "Parcial 1", "gradeValue": 4.0, "percentage": 100}
                                """))
                .andExpect(status().isCreated());

        // cut1=4.0×40=160, pending=60%; required=(4.0×100-160)/60=4.0
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Aprobar Cálculo", "targetGrade": 4.0, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requiredGrade").value(4.0))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    @Test
    @DisplayName("Should set achievable=false when goal is mathematically impossible")
    void shouldSetNotAchievableWhenImpossible() throws Exception {
        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"activityName": "Parcial 1", "gradeValue": 1.0, "percentage": 100}
                                """))
                .andExpect(status().isCreated());

        // target=4.5; cut1=1.0×40=40; pending=60; required=(4.5×100-40)/60≈6.83>5
        mockMvc.perform(put(GOALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("studentId", STUDENT_ID)
                        .content("""
                                {"goalName": "Meta alta", "targetGrade": 4.5, "subjectId": %d}
                                """.formatted(subjectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.achievable").value(false));
    }
}
