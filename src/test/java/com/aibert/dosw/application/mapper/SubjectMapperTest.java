package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.SubjectResponseDTO;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubjectMapper Tests")
class SubjectMapperTest {

    private Subject subject;
    private SubjectMapper subjectMapper;

    @BeforeEach
    void setUp() {
        subjectMapper = new SubjectMapperImpl();
        
        EvaluationCut cut1 = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .grade(4.5)
            .build();

        EvaluationCut cut2 = EvaluationCut.builder()
            .id(2L)
            .cutName("Corte 2")
            .cutPercentage(60.0)
            .grade(3.5)
            .build();

        subject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(cut1, cut2))
            .build();
    }

    @Test
    @DisplayName("Should map Subject to SubjectResponseDTO")
    void shouldMapSubjectToSubjectResponseDTO() {
        SubjectResponseDTO responseDTO = subjectMapper.toResponseDTO((com.aibert.dosw.domain.model.Subject) subject);
        
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Cálculo Diferencial", responseDTO.getSubjectName());
        assertEquals(4, responseDTO.getCredits());
        assertEquals("Prof. Ramírez", responseDTO.getTeacherName());
        assertEquals("2025-1", responseDTO.getSemester());
        assertEquals(2, responseDTO.getEvaluationCuts().size());
    }

    @Test
    @DisplayName("Should map Subject with no evaluation cuts")
    void shouldMapSubjectWithNoEvaluationCuts() {
        Subject subjectWithoutCuts = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(2)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();

        SubjectResponseDTO responseDTO = subjectMapper.toResponseDTO((com.aibert.dosw.domain.model.Subject) subjectWithoutCuts);
        
        assertNotNull(responseDTO);
        assertEquals("Materia Simple", responseDTO.getSubjectName());
        assertTrue(responseDTO.getEvaluationCuts().isEmpty());
    }

    @Test
    @DisplayName("Should map list of Subjects to list of SubjectResponseDTOs")
    void shouldMapListOfSubjectsToListOfSubjectResponseDTOs() {
        Subject subject2 = Subject.builder()
            .id(2L)
            .studentId("student-test")
            .subjectName("Física General")
            .credits(3)
            .teacherName("Prof. Torres")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();

        List<Subject> subjects = Arrays.asList(subject, subject2);
        List<SubjectResponseDTO> responseDTOs = subjectMapper.toResponseDTOList(subjects);
        
        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals("Cálculo Diferencial", responseDTOs.get(0).getSubjectName());
        assertEquals("Física General", responseDTOs.get(1).getSubjectName());
    }

    @Test
    @DisplayName("Should handle empty list of subjects")
    void shouldHandleEmptyListOfSubjects() {
        List<Subject> emptySubjects = Collections.emptyList();
        List<SubjectResponseDTO> responseDTOs = subjectMapper.toResponseDTOList(emptySubjects);
        
        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    @DisplayName("Should handle null subject")
    void shouldHandleNullSubject() {
        SubjectResponseDTO responseDTO = subjectMapper.toResponseDTO((com.aibert.dosw.domain.model.Subject) null);
        
        assertNull(responseDTO);
    }

    @Test
    @DisplayName("Should handle null list of subjects")
    void shouldHandleNullListOfSubjects() {
        List<SubjectResponseDTO> responseDTOs = subjectMapper.toResponseDTOList(null);
        
        assertNull(responseDTOs);
    }

    @Test
    @DisplayName("Should preserve evaluation cut grades in mapping")
    void shouldPreserveEvaluationCutGradesInMapping() {
        SubjectResponseDTO responseDTO = subjectMapper.toResponseDTO((com.aibert.dosw.domain.model.Subject) subject);
        
        assertNotNull(responseDTO);
        assertEquals(2, responseDTO.getEvaluationCuts().size());
        
        var cut1 = responseDTO.getEvaluationCuts().get(0);
        assertEquals(1L, cut1.getId());
        assertEquals("Corte 1", cut1.getCutName());
        assertEquals(40.0, cut1.getCutPercentage());
        assertEquals(4.5, cut1.getGrade());
        
        var cut2 = responseDTO.getEvaluationCuts().get(1);
        assertEquals(2L, cut2.getId());
        assertEquals("Corte 2", cut2.getCutName());
        assertEquals(60.0, cut2.getCutPercentage());
        assertEquals(3.5, cut2.getGrade());
    }
}
