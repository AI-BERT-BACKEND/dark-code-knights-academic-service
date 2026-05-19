package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.AcademicServiceApplication;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.ScheduleAvailabilityJpaRepository;
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
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AcademicServiceApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Schedule Availability Integration Tests (AIB-10)")
class ScheduleAvailabilityIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ScheduleAvailabilityJpaRepository jpaRepository;

    private MockMvc mockMvc;

    private static final String STUDENT_ID = "sched-student";
    private static final String URL = "/api/v1/students/schedule-availability";

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // ─── PUT — create ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create availability with all fields")
    void shouldCreateAvailabilityWithAllFields() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {
                                  "freeTimeHours": 4.0,
                                  "restHours": 8.0,
                                  "personalTimeHours": 2.0,
                                  "socialTimeHours": 2.0,
                                  "maxStudyHoursPerDay": 6.0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.configId", notNullValue()))
                .andExpect(jsonPath("$.data.freeTimeHours").value(4.0))
                .andExpect(jsonPath("$.data.restHours").value(8.0))
                .andExpect(jsonPath("$.data.maxStudyHoursPerDay").value(6.0));
    }

    @Test
    @DisplayName("Should create availability with only some fields (all optional)")
    void shouldCreateWithPartialFields() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {
                                  "restHours": 8.0,
                                  "maxStudyHoursPerDay": 6.0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.restHours").value(8.0))
                .andExpect(jsonPath("$.data.maxStudyHoursPerDay").value(6.0));
    }

    @Test
    @DisplayName("Should create availability with empty body (all fields null)")
    void shouldCreateWithEmptyBody() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ─── PUT — update ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing availability and preserve configId")
    void shouldUpdateExistingAvailability() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"freeTimeHours": 3.0, "restHours": 7.0}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"freeTimeHours": 5.0, "restHours": 9.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freeTimeHours").value(5.0))
                .andExpect(jsonPath("$.data.restHours").value(9.0));

        long count = jpaRepository.count();
        assert count == 1L;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return availability after saving")
    void shouldReturnAvailabilityAfterSaving() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"freeTimeHours": 4.0, "maxStudyHoursPerDay": 6.0}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL)
                        .header("X-Student-Id", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.freeTimeHours").value(4.0))
                .andExpect(jsonPath("$.data.maxStudyHoursPerDay").value(6.0));
    }

    @Test
    @DisplayName("Should return 404 when no availability configured")
    void shouldReturn404WhenNotConfigured() throws Exception {
        mockMvc.perform(get(URL)
                        .header("X-Student-Id", "unknown-student"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─── Validation: FA-01 maxStudyHoursPerDay > 15 ───────────────────────────

    @Test
    @DisplayName("Should return 400 when maxStudyHoursPerDay exceeds 15")
    void shouldReturn400WhenMaxStudyHoursExceeds15() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"maxStudyHoursPerDay": 16.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─── Validation: FA-02 any field <= 0 ────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when freeTimeHours is zero")
    void shouldReturn400WhenFieldIsZero() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"freeTimeHours": 0.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when restHours is negative")
    void shouldReturn400WhenFieldIsNegative() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {"restHours": -1.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─── Validation: FA-03 sum > 24 ──────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when total hours exceed 24")
    void shouldReturn400WhenSumExceeds24() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Student-Id", STUDENT_ID)
                        .content("""
                                {
                                  "freeTimeHours": 8.0,
                                  "restHours": 8.0,
                                  "personalTimeHours": 5.0,
                                  "socialTimeHours": 4.0,
                                  "maxStudyHoursPerDay": 4.0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error", containsString("24")));
    }

    // ─── Missing header ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing on PUT")
    void shouldReturn400WhenHeaderMissingOnPut() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when X-Student-Id header is missing on GET")
    void shouldReturn400WhenHeaderMissingOnGet() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isBadRequest());
    }
}
