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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private Long subjectId;
    private Long cut1Id;
    private Long cut2Id;

    @BeforeEach
    void setUp() throws Exception {
        gradeJpaRepository.deleteAll();
        goalJpaRepository.deleteAll();
        subjectJpaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String subjectJson = """
            {
              "subjectName": "Cálculo Integral",
              "credits": 4,
              "teacherName": "Prof. García",
              "semester": "%s",
              "schedule": "LUNES 08:00-10:00, MIERCOLES 08:00-10:00",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 40 },
                { "cutName": "Corte 2", "cutPercentage": 60 }
              ]
            }
            """.formatted(SEMESTER);

        String response = mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content(subjectJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        subjectId = objectMapper.readTree(response).get("data").get("id").asLong();
        cut1Id = objectMapper.readTree(response).get("data").get("evaluationCuts").get(0).get("id").asLong();
        cut2Id = objectMapper.readTree(response).get("data").get("evaluationCuts").get(1).get("id").asLong();
    }

    // ─── PUT /api/v1/subjects/{id}/goal ───────────────────────────────────────

    @Test
    @DisplayName("Should create goal successfully when no grades exist yet")
    void shouldCreateGoalSuccessfully() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.0 }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Meta académica guardada exitosamente"))
                .andExpect(jsonPath("$.data.targetGrade").value(4.0))
                .andExpect(jsonPath("$.data.subjectId").value(subjectId.intValue()))
                .andExpect(jsonPath("$.data.subjectName").value("Cálculo Integral"))
                .andExpect(jsonPath("$.data.semester").value(SEMESTER))
                .andExpect(jsonPath("$.data.currentAverage").doesNotExist())
                // pending=100% → required=(4.0×100)/100 = 4.0
                .andExpect(jsonPath("$.data.requiredGrade").value(4.0))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    @Test
    @DisplayName("Should update existing goal when called again")
    void shouldUpdateExistingGoal() throws Exception {
        // Create goal
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 3.0 }
                            """))
                .andExpect(status().isOk());

        // Update goal
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.5 }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.targetGrade").value(4.5));

        // Only one goal should exist in the DB
        assertEquals(1, goalJpaRepository.count());
    }

    @Test
    @DisplayName("Should return progress with requiredGrade after partial grading")
    void shouldShowRequiredGradeAfterPartialGrading() throws Exception {
        // Register grade for cut1: gradeValue=4.0, percentage=100 (of cut1)
        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "activityName": "Parcial 1", "gradeValue": 4.0, "percentage": 100 }
                            """))
                .andExpect(status().isCreated());

        // Set goal 4.0
        // cut1 weighted = 4.0×40 = 160; cut2 pending = 60%
        // required = (4.0×100 - 160) / 60 = 240/60 = 4.0
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.0 }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requiredGrade").value(4.0))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    @Test
    @DisplayName("Should return achievable=false when goal cannot be reached")
    void shouldReturnNotAchievableWhenGoalImpossible() throws Exception {
        // Grade cut1 very low: 1.0
        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "activityName": "Parcial 1", "gradeValue": 1.0, "percentage": 100 }
                            """))
                .andExpect(status().isCreated());

        // target=4.5; cut1=1.0×40=40; pending=60
        // required=(4.5×100 - 40)/60 = 410/60 ≈ 6.83 > 5.0
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.5 }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.achievable").value(false));
    }

    @Test
    @DisplayName("Should return 400 when targetGrade is null")
    void shouldReturn400WhenTargetGradeIsNull() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            {}
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("La nota objetivo es obligatoria")));
    }

    @Test
    @DisplayName("Should return 400 when targetGrade exceeds 5.0")
    void shouldReturn400WhenTargetGradeExceedsFive() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 5.1 }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("5.0")));
    }

    @Test
    @DisplayName("Should return 400 when targetGrade is below 0.0")
    void shouldReturn400WhenTargetGradeIsNegative() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": -0.1 }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("0.0")));
    }

    @Test
    @DisplayName("Should return 404 when subject does not belong to the student")
    void shouldReturn404WhenSubjectNotOwnedByStudent() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", "another-student")
                        .content("""
                            { "targetGrade": 4.0 }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString(subjectId.toString())));
    }

    // ─── GET /api/v1/subjects/{id}/goal ──────────────────────────────────────

    @Test
    @DisplayName("Should return goal progress when goal exists")
    void shouldReturnGoalProgress() throws Exception {
        // Create goal
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 3.5 }
                            """))
                .andExpect(status().isOk());

        // Get goal
        mockMvc.perform(get("/api/v1/subjects/{id}/goal", subjectId)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.targetGrade").value(3.5))
                .andExpect(jsonPath("$.data.subjectName").value("Cálculo Integral"))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    @Test
    @DisplayName("Should return 404 when no goal exists for subject")
    void shouldReturn404WhenGoalDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/subjects/{id}/goal", subjectId)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString(subjectId.toString())));
    }

    @Test
    @DisplayName("Should return achievable=true and null requiredGrade when all cuts graded and target met")
    void shouldReturnAchievableWithNullRequiredGradeWhenAllCutsGraded() throws Exception {
        // Grade both cuts
        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "activityName": "Parcial 1", "gradeValue": 4.0, "percentage": 100 }
                            """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/subjects/{sid}/cuts/{cid}/grades", subjectId, cut2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "activityName": "Parcial 2", "gradeValue": 4.0, "percentage": 100 }
                            """))
                .andExpect(status().isCreated());

        // Set goal 3.5 — current average 4.0×40 + 4.0×60 = 4.0 ≥ 3.5
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 3.5 }
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/subjects/{id}/goal", subjectId)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requiredGrade").doesNotExist())
                .andExpect(jsonPath("$.data.currentAverage").value(4.0))
                .andExpect(jsonPath("$.data.achievable").value(true));
    }

    // ─── GET /api/v1/academic/goals ──────────────────────────────────────────

    @Test
    @DisplayName("Should return empty list when student has no goals")
    void shouldReturnEmptyListWhenNoGoals() throws Exception {
        mockMvc.perform(get("/api/v1/academic/goals")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(containsString(SEMESTER)));
    }

    @Test
    @DisplayName("Should return all goals for the semester")
    void shouldReturnAllGoalsForSemester() throws Exception {
        // Create a second subject in the same semester
        String secondSubjectJson = """
            {
              "subjectName": "Álgebra Lineal",
              "credits": 3,
              "teacherName": "Prof. López",
              "semester": "%s",
              "schedule": "JUEVES 10:00-12:00",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """.formatted(SEMESTER);

        String secondResponse = mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content(secondSubjectJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long secondSubjectId = objectMapper.readTree(secondResponse).get("data").get("id").asLong();

        // Set goals for both subjects
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.0 }
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/subjects/{id}/goal", secondSubjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 3.5 }
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/academic/goals")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("Should not include goals from a different semester in getAllGoals")
    void shouldNotIncludeGoalsFromDifferentSemester() throws Exception {
        // Create a subject in 2025-2
        String otherSemesterJson = """
            {
              "subjectName": "Física Moderna",
              "credits": 3,
              "teacherName": "Prof. Ruiz",
              "semester": "2025-2",
              "schedule": "VIERNES 14:00-16:00",
              "evaluationCuts": [
                { "cutName": "Corte 1", "cutPercentage": 100 }
              ]
            }
            """;
        String otherResponse = mockMvc.perform(post("/api/v1/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content(otherSemesterJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long otherSubjectId = objectMapper.readTree(otherResponse).get("data").get("id").asLong();

        // Set goals for subjects in both semesters
        mockMvc.perform(put("/api/v1/subjects/{id}/goal", subjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 4.0 }
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/subjects/{id}/goal", otherSubjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                            { "targetGrade": 3.0 }
                            """))
                .andExpect(status().isOk());

        // Query for 2025-1 only
        mockMvc.perform(get("/api/v1/academic/goals")
                        .header("X-Student-Id", STUDENT_ID)
                        .param("semester", SEMESTER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].semester").value(SEMESTER));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void assertEquals(long expected, long actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private void assertEquals(int expected, long actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
