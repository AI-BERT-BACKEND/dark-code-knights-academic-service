package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.AcademicSummaryDTO;
import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicSummaryUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicController Tests")
class AcademicControllerTest {

    @Mock
    private GetAcademicSummaryUseCase getAcademicSummaryUseCase;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private AcademicController academicController;

    private String testStudentId;
    private Subject testSubject1;
    private Subject testSubject2;
    private EvaluationCut testCut1;
    private EvaluationCut testCut2;

    @BeforeEach
    void setUp() {
        testStudentId = "student123";
        
        testCut1 = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
            
        testCut2 = EvaluationCut.builder()
            .id(2L)
            .cutName("Corte 2")
            .cutPercentage(40.0)
            .grade(null)
            .build();
        
        testSubject1 = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(testCut1, testCut2))
            .build();
            
        testSubject2 = Subject.builder()
            .id(2L)
            .studentId(testStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
    }

    @Test
    @DisplayName("Should get academic summary successfully with subjects")
    void shouldGetAcademicSummarySuccessfullyWithSubjects() {
        // Given
        List<Subject> subjects = List.of(testSubject1, testSubject2);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(testSubject1.getEvaluationCuts())).thenReturn(List.of());
        when(subjectMapper.toResponseCutDTOList(testSubject2.getEvaluationCuts())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiResponse<AcademicSummaryDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());
        
        AcademicSummaryDTO summary = apiResponse.getData();
        assertNotNull(summary);
        assertEquals(testStudentId, summary.getStudentId());
        assertEquals(1.2, summary.getAcademicGpa()); // Only one subject has grades: 4.0 * 30.0 / 100.0 = 1.2
        assertEquals(2, summary.getSubjects().size());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
        verify(subjectMapper, times(1)).toResponseCutDTOList(testSubject1.getEvaluationCuts());
        verify(subjectMapper, times(1)).toResponseCutDTOList(testSubject2.getEvaluationCuts());
    }

    @Test
    @DisplayName("Should get academic summary with empty subjects list")
    void shouldGetAcademicSummaryWithEmptySubjectsList() {
        // Given
        List<Subject> emptySubjects = List.of();
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(emptySubjects);

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiResponse<AcademicSummaryDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("El estudiante no tiene materias registradas", apiResponse.getMessage());
        
        AcademicSummaryDTO summary = apiResponse.getData();
        assertNotNull(summary);
        assertEquals(testStudentId, summary.getStudentId());
        assertNull(summary.getAcademicGpa());
        assertTrue(summary.getSubjects().isEmpty());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
        verify(subjectMapper, never()).toResponseCutDTOList(any());
    }

    @Test
    @DisplayName("Should calculate GPA correctly with multiple graded subjects")
    void shouldCalculateGpaCorrectlyWithMultipleGradedSubjects() {
        // Given
        EvaluationCut gradedCut1 = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(50.0)
            .grade(4.0)
            .build();
        EvaluationCut gradedCut2 = EvaluationCut.builder()
            .id(2L)
            .cutName("Corte 2")
            .cutPercentage(50.0)
            .grade(3.0)
            .build();
            
        Subject gradedSubject1 = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(gradedCut1))
            .build();
            
        Subject gradedSubject2 = Subject.builder()
            .id(2L)
            .studentId(testStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of(gradedCut2))
            .build();
        
        List<Subject> subjects = List.of(gradedSubject1, gradedSubject2);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(any())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        AcademicSummaryDTO summary = response.getBody().getData();
        assertNotNull(summary);
        assertEquals(1.75, summary.getAcademicGpa()); // Subject 1: 4.0 * 50% = 2.0, Subject 2: 3.0 * 50% = 1.5, Average: (2.0 + 1.5) / 2 = 1.75
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
    }

    @Test
    @DisplayName("Should handle subjects with no grades")
    void shouldHandleSubjectsNoGrades() {
        // Given
        EvaluationCut noGradeCut = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(100.0)
            .grade(null)
            .build();
            
        Subject subjectNoGrades = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(noGradeCut))
            .build();
        
        List<Subject> subjects = List.of(subjectNoGrades);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(any())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        AcademicSummaryDTO summary = response.getBody().getData();
        assertNotNull(summary);
        assertNull(summary.getAcademicGpa()); // No grades, so GPA is null
        assertEquals(1, summary.getSubjects().size());
        assertNull(summary.getSubjects().get(0).getOverallAverage());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
    }

    @Test
    @DisplayName("Should handle mixed graded and non-graded subjects")
    void shouldHandleMixedGradedAndNonGradedSubjects() {
        // Given
        EvaluationCut gradedCut = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(100.0)
            .grade(4.0)
            .build();
            
        EvaluationCut noGradeCut = EvaluationCut.builder()
            .id(2L)
            .cutName("Corte 1")
            .cutPercentage(100.0)
            .grade(null)
            .build();
            
        Subject gradedSubject = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(gradedCut))
            .build();
            
        Subject nonGradedSubject = Subject.builder()
            .id(2L)
            .studentId(testStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of(noGradeCut))
            .build();
        
        List<Subject> subjects = List.of(gradedSubject, nonGradedSubject);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(any())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        AcademicSummaryDTO summary = response.getBody().getData();
        assertNotNull(summary);
        assertEquals(4.0, summary.getAcademicGpa()); // Only graded subject counts: 4.0
        assertEquals(2, summary.getSubjects().size());
        assertEquals(4.0, summary.getSubjects().get(0).getOverallAverage());
        assertNull(summary.getSubjects().get(1).getOverallAverage());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
    }

    @Test
    @DisplayName("Should handle complex grade calculations")
    void shouldHandleComplexGradeCalculations() {
        // Given
        EvaluationCut cut1 = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(30.0)
            .grade(3.5)
            .build();
        EvaluationCut cut2 = EvaluationCut.builder()
            .id(2L)
            .cutName("Corte 2")
            .cutPercentage(40.0)
            .grade(4.0)
            .build();
        EvaluationCut cut3 = EvaluationCut.builder()
            .id(3L)
            .cutName("Corte 3")
            .cutPercentage(30.0)
            .grade(4.5)
            .build();
            
        Subject complexSubject = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(cut1, cut2, cut3))
            .build();
        
        List<Subject> subjects = List.of(complexSubject);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(any())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        AcademicSummaryDTO summary = response.getBody().getData();
        assertNotNull(summary);
        // Expected: (3.5*30 + 4.0*40 + 4.5*30) / 100 = (105 + 160 + 135) / 100 = 400 / 100 = 4.0
        assertEquals(4.0, summary.getAcademicGpa());
        assertEquals(4.0, summary.getSubjects().get(0).getOverallAverage());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
    }

    @Test
    @DisplayName("Should handle null student ID gracefully")
    void shouldHandleNullStudentIdGracefully() {
        // Given
        String nullStudentId = null;
        when(getAcademicSummaryUseCase.getSummary(nullStudentId)).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(nullStudentId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("El estudiante no tiene materias registradas", response.getBody().getMessage());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(nullStudentId);
    }

    @Test
    @DisplayName("Should handle empty student ID")
    void shouldHandleEmptyStudentId() {
        // Given
        String emptyStudentId = "";
        when(getAcademicSummaryUseCase.getSummary(emptyStudentId)).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(emptyStudentId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("El estudiante no tiene materias registradas", response.getBody().getMessage());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(emptyStudentId);
    }

    @Test
    @DisplayName("Should verify response structure")
    void shouldVerifyResponseStructure() {
        // Given
        List<Subject> subjects = List.of(testSubject1);
        when(getAcademicSummaryUseCase.getSummary(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseCutDTOList(any())).thenReturn(List.of());

        // When
        ResponseEntity<ApiResponse<AcademicSummaryDTO>> response = 
            academicController.getSummary(testStudentId);

        // Then
        assertNotNull(response);
        assertTrue(response.hasBody());
        
        ApiResponse<AcademicSummaryDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getData());
        assertNotNull(apiResponse.getMessage());
        
        AcademicSummaryDTO summary = apiResponse.getData();
        assertNotNull(summary.getStudentId());
        assertNotNull(summary.getSubjects());
        
        verify(getAcademicSummaryUseCase, times(1)).getSummary(testStudentId);
    }
}
