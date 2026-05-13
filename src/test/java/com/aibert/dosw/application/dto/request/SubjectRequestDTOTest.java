package com.aibert.dosw.application.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubjectRequestDTO Tests")
class SubjectRequestDTOTest {

    private SubjectRequestDTO subjectRequestDTO;
    private EvaluationCutDTO evaluationCutDTO;

    @BeforeEach
    void setUp() {
        evaluationCutDTO = EvaluationCutDTO.builder()
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .build();

        subjectRequestDTO = SubjectRequestDTO.builder()
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(evaluationCutDTO))
            .build();
    }

    @Test
    @DisplayName("Should create SubjectRequestDTO with builder")
    void shouldCreateSubjectRequestDTOWithBuilder() {
        assertNotNull(subjectRequestDTO);
        assertEquals("Cálculo Diferencial", subjectRequestDTO.getSubjectName());
        assertEquals(4, subjectRequestDTO.getCredits());
        assertEquals("Prof. Ramírez", subjectRequestDTO.getTeacherName());
        assertEquals("2025-1", subjectRequestDTO.getSemester());
        assertEquals(1, subjectRequestDTO.getEvaluationCuts().size());
    }

    @Test
    @DisplayName("Should create SubjectRequestDTO with no args constructor")
    void shouldCreateSubjectRequestDTOWithNoArgsConstructor() {
        SubjectRequestDTO dto = new SubjectRequestDTO();
        
        assertNotNull(dto);
        assertNull(dto.getSubjectName());
        assertNull(dto.getCredits());
        assertNull(dto.getTeacherName());
        assertNull(dto.getSemester());
        assertNull(dto.getEvaluationCuts());
    }

    @Test
    @DisplayName("Should use setters and getters correctly")
    void shouldUseSettersAndGettersCorrectly() {
        SubjectRequestDTO dto = new SubjectRequestDTO();
        
        dto.setSubjectName("Física General");
        dto.setCredits(3);
        dto.setTeacherName("Prof. Torres");
        dto.setSemester("2025-1");
        dto.setEvaluationCuts(Collections.emptyList());
        
        assertEquals("Física General", dto.getSubjectName());
        assertEquals(3, dto.getCredits());
        assertEquals("Prof. Torres", dto.getTeacherName());
        assertEquals("2025-1", dto.getSemester());
        assertTrue(dto.getEvaluationCuts().isEmpty());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        EvaluationCutDTO cut1 = EvaluationCutDTO.builder()
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .build();

        EvaluationCutDTO cut2 = EvaluationCutDTO.builder()
            .cutName("Corte 2")
            .cutPercentage(60.0)
            .build();

        SubjectRequestDTO dto1 = SubjectRequestDTO.builder()
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1, cut2))
            .build();
        
        SubjectRequestDTO dto2 = SubjectRequestDTO.builder()
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1, cut2))
            .build();
        
        SubjectRequestDTO dto3 = SubjectRequestDTO.builder()
            .subjectName("Física General")
            .credits(3)
            .teacherName("Prof. Torres")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1))
            .build();
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        String toString = subjectRequestDTO.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("SubjectRequestDTO"));
        assertTrue(toString.contains("subjectName=Cálculo Diferencial"));
        assertTrue(toString.contains("credits=4"));
    }

    @org.junit.jupiter.api.Test
    void equalsWhenCreditsDiffers() {
        SubjectRequestDTO a = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        SubjectRequestDTO b = SubjectRequestDTO.builder().subjectName("S").credits(5).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        assertNotEquals(a, b);
    }

    @org.junit.jupiter.api.Test
    void equalsWhenTeacherNameDiffers() {
        SubjectRequestDTO a = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T1").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        SubjectRequestDTO b = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T2").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        assertNotEquals(a, b);
    }

    @org.junit.jupiter.api.Test
    void equalsWhenSemesterDiffers() {
        SubjectRequestDTO a = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        SubjectRequestDTO b = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-2").evaluationCuts(java.util.List.of()).build();
        assertNotEquals(a, b);
    }

    @org.junit.jupiter.api.Test
    void equalsWhenEvalCutsDiffer() {
        EvaluationCutDTO c1 = EvaluationCutDTO.builder().cutName("A").cutPercentage(50.0).build();
        EvaluationCutDTO c2 = EvaluationCutDTO.builder().cutName("B").cutPercentage(50.0).build();
        SubjectRequestDTO a = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of(c1)).build();
        SubjectRequestDTO b = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of(c2)).build();
        assertNotEquals(a, b);
    }

    @org.junit.jupiter.api.Test
    void equalsWithAllNullFields() {
        assertEquals(new SubjectRequestDTO(), new SubjectRequestDTO());
    }

    @org.junit.jupiter.api.Test
    void equalsWithNullSubjectNameVsNonNull() {
        SubjectRequestDTO withNull = new SubjectRequestDTO();
        SubjectRequestDTO withValue = SubjectRequestDTO.builder().subjectName("S").credits(4).teacherName("T").semester("2025-1").evaluationCuts(java.util.List.of()).build();
        assertNotEquals(withNull, withValue);
        assertNotEquals(withValue, withNull);
    }

    @org.junit.jupiter.api.Test
    void hashCodeWithNullFields() {
        assertEquals(new SubjectRequestDTO().hashCode(), new SubjectRequestDTO().hashCode());
    }
}
