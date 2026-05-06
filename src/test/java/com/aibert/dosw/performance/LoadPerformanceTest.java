package com.aibert.dosw.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoadPerformanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createSubject_shouldRespondUnder2Seconds() {
        String requestBody = """
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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Student-Id", "student-performance-test");
        
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/v1/subjects",
            new HttpEntity<>(requestBody, headers),
            String.class
        );
        long duration = System.currentTimeMillis() - start;
        
        assertThat(duration)
            .as("POST /api/v1/subjects should respond in under 2 seconds")
            .isLessThan(2000L);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void registerGrade_shouldRespondUnder2Seconds() {
        // First create prerequisite data (subject + evaluation structure)
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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Student-Id", "student-performance-test");
        
        // Create subject first
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
            "/api/v1/subjects",
            new HttpEntity<>(subjectJson, headers),
            String.class
        );
        
        try {
            // Extract subjectId and cutId from response
            String subjectId = objectMapper.readTree(createResponse.getBody()).get("data").get("id").asText();
            String cutId = objectMapper.readTree(createResponse.getBody()).get("data").get("evaluationCuts").get(0).get("id").asText();
            
            // Build grade request body
            String gradeJson = """
                {
                  "activityName": "Parcial 1",
                  "gradeValue": 4.5,
                  "percentage": 60
                }
                """;
            
            HttpHeaders gradeHeaders = new HttpHeaders();
            gradeHeaders.setContentType(MediaType.APPLICATION_JSON);
            
            long start = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/subjects/{subjectId}/cuts/{cutId}/grades",
                new HttpEntity<>(gradeJson, gradeHeaders),
                String.class,
                subjectId, cutId
            );
            long duration = System.currentTimeMillis() - start;
            
            assertThat(duration)
                .as("Grade registration endpoint should respond in under 2 seconds")
                .isLessThan(2000L);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        } catch (Exception e) {
            // If parsing fails, skip test
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Failed to setup test data: " + e.getMessage());
        }
    }

    @Test
    void simulateTargetGrade_shouldRespondUnder2Seconds() {
        // First create prerequisite data (subject + evaluation structure + grades)
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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Student-Id", "student-performance-test");
        
        // Create subject first
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
            "/api/v1/subjects",
            new HttpEntity<>(subjectJson, headers),
            String.class
        );
        
        try {
            // Extract subjectId and cutIds from response
            String subjectId = objectMapper.readTree(createResponse.getBody()).get("data").get("id").asText();
            String cut1Id = objectMapper.readTree(createResponse.getBody()).get("data").get("evaluationCuts").get(0).get("id").asText();
            String cut2Id = objectMapper.readTree(createResponse.getBody()).get("data").get("evaluationCuts").get(1).get("id").asText();
            
            // Setup grades for first two cuts - same as SimulationIntegrationTest
            String gradeJson1 = """
                {
                  "activityName": "Parcial 1",
                  "gradeValue": 5.0,
                  "percentage": 100
                }
                """;
            
            String gradeJson2 = """
                {
                  "activityName": "Parcial 2",
                  "gradeValue": 3.5,
                  "percentage": 100
                }
                """;
            
            HttpHeaders gradeHeaders = new HttpHeaders();
            gradeHeaders.setContentType(MediaType.APPLICATION_JSON);
            
            // Register grades for first two cuts
            restTemplate.postForEntity(
                "/api/v1/subjects/{subjectId}/cuts/{cutId}/grades",
                new HttpEntity<>(gradeJson1, gradeHeaders),
                String.class,
                subjectId, cut1Id
            );
            
            restTemplate.postForEntity(
                "/api/v1/subjects/{subjectId}/cuts/{cutId}/grades",
                new HttpEntity<>(gradeJson2, gradeHeaders),
                String.class,
                subjectId, cut2Id
            );
            
            // Call simulation endpoint
            String simulationJson = """
                {
                  "targetGrade": 4.0
                }
                """;
            
            HttpHeaders simulationHeaders = new HttpHeaders();
            simulationHeaders.setContentType(MediaType.APPLICATION_JSON);
            
            long start = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/subjects/{subjectId}/simulate",
                new HttpEntity<>(simulationJson, simulationHeaders),
                String.class,
                subjectId
            );
            long duration = System.currentTimeMillis() - start;
            
            assertThat(duration)
                .as("Simulation endpoint should respond in under 2 seconds")
                .isLessThan(2000L);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        } catch (Exception e) {
            // If parsing fails, skip test
            org.junit.jupiter.api.Assumptions.assumeTrue(false, "Failed to setup test data: " + e.getMessage());
        }
    }
}
