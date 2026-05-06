package com.aibert.dosw.application.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvaluationCutDTO Tests")
class EvaluationCutDTOTest {

    private EvaluationCutDTO evaluationCutDTO;

    @BeforeEach
    void setUp() {
        evaluationCutDTO = EvaluationCutDTO.builder()
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .build();
    }

    @Test
    @DisplayName("Should create EvaluationCutDTO with builder")
    void shouldCreateEvaluationCutDTOWithBuilder() {
        assertNotNull(evaluationCutDTO);
        assertEquals("Corte 1", evaluationCutDTO.getCutName());
        assertEquals(40.0, evaluationCutDTO.getCutPercentage());
    }

    @Test
    @DisplayName("Should create EvaluationCutDTO with all args constructor")
    void shouldCreateEvaluationCutDTOWithAllArgsConstructor() {
        EvaluationCutDTO dto = new EvaluationCutDTO("Corte 2", 60.0);
        
        assertNotNull(dto);
        assertEquals("Corte 2", dto.getCutName());
        assertEquals(60.0, dto.getCutPercentage());
    }

    @Test
    @DisplayName("Should create EvaluationCutDTO with no args constructor")
    void shouldCreateEvaluationCutDTOWithNoArgsConstructor() {
        EvaluationCutDTO dto = new EvaluationCutDTO();
        
        assertNotNull(dto);
        assertNull(dto.getCutName());
        assertNull(dto.getCutPercentage());
    }

    @Test
    @DisplayName("Should use setters and getters correctly")
    void shouldUseSettersAndGettersCorrectly() {
        EvaluationCutDTO dto = new EvaluationCutDTO();
        
        dto.setCutName("Corte 3");
        dto.setCutPercentage(50.0);
        
        assertEquals("Corte 3", dto.getCutName());
        assertEquals(50.0, dto.getCutPercentage());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        EvaluationCutDTO dto1 = EvaluationCutDTO.builder()
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .build();
        
        EvaluationCutDTO dto2 = EvaluationCutDTO.builder()
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .build();
        
        EvaluationCutDTO dto3 = EvaluationCutDTO.builder()
            .cutName("Corte 2")
            .cutPercentage(60.0)
            .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        String toString = evaluationCutDTO.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("EvaluationCutDTO"));
        assertTrue(toString.contains("cutName=Corte 1"));
        assertTrue(toString.contains("cutPercentage=40.0"));
    }
}
