package com.aibert.dosw.application.dto.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubjectResponseDTO Tests")
class SubjectResponseDTOTest {

    private SubjectResponseDTO subjectResponseDTO;

    @BeforeEach
    void setUp() {
        EvaluationCutResponseDTO cutDTO = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .grade(4.5)
            .build();

        subjectResponseDTO = SubjectResponseDTO.builder()
            .id(1L)
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cutDTO))
            .build();
    }

    @Test
    @DisplayName("Should create SubjectResponseDTO with builder")
    void shouldCreateSubjectResponseDTOWithBuilder() {
        assertNotNull(subjectResponseDTO);
        assertEquals(1L, subjectResponseDTO.getId());
        assertEquals("Cálculo Diferencial", subjectResponseDTO.getSubjectName());
        assertEquals(4, subjectResponseDTO.getCredits());
        assertEquals("Prof. Ramírez", subjectResponseDTO.getTeacherName());
        assertEquals("2025-1", subjectResponseDTO.getSemester());
        assertEquals(1, subjectResponseDTO.getEvaluationCuts().size());
    }

    @Test
    @DisplayName("Should create SubjectResponseDTO with no args constructor")
    void shouldCreateSubjectResponseDTOWithNoArgsConstructor() {
        SubjectResponseDTO dto = new SubjectResponseDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getSubjectName());
        assertNull(dto.getCredits());
        assertNull(dto.getTeacherName());
        assertNull(dto.getSemester());
        assertNull(dto.getEvaluationCuts());
    }

    @Test
    @DisplayName("Should use setters and getters correctly")
    void shouldUseSettersAndGettersCorrectly() {
        SubjectResponseDTO dto = new SubjectResponseDTO();
        
        dto.setId(2L);
        dto.setSubjectName("Física General");
        dto.setCredits(3);
        dto.setTeacherName("Prof. Torres");
        dto.setSemester("2025-1");
        dto.setEvaluationCuts(Collections.emptyList());
        
        assertEquals(2L, dto.getId());
        assertEquals("Física General", dto.getSubjectName());
        assertEquals(3, dto.getCredits());
        assertEquals("Prof. Torres", dto.getTeacherName());
        assertEquals("2025-1", dto.getSemester());
        assertTrue(dto.getEvaluationCuts().isEmpty());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        EvaluationCutResponseDTO cut1 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .grade(4.5)
            .build();

        SubjectResponseDTO dto1 = SubjectResponseDTO.builder()
            .id(1L)
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1))
            .build();
        
        SubjectResponseDTO dto2 = SubjectResponseDTO.builder()
            .id(1L)
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1))
            .build();
        
        SubjectResponseDTO dto3 = SubjectResponseDTO.builder()
            .id(2L)
            .subjectName("Física General")
            .credits(3)
            .teacherName("Prof. Torres")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        String toString = subjectResponseDTO.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("SubjectResponseDTO"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("subjectName=Cálculo Diferencial"));
        assertTrue(toString.contains("credits=4"));
    }
}
