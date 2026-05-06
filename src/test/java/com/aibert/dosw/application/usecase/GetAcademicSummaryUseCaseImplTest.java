package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAcademicSummaryUseCaseImpl Tests")
class GetAcademicSummaryUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private GetAcademicSummaryUseCaseImpl getAcademicSummaryUseCase;

    private Subject testSubject1;
    private Subject testSubject2;
    private List<Subject> testSubjects;

    @BeforeEach
    void setUp() {
        testSubject1 = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of())
            .build();
            
        testSubject2 = Subject.builder()
            .id(2L)
            .studentId("student123")
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
            
        testSubjects = List.of(testSubject1, testSubject2);
    }

    @Test
    @DisplayName("Should get academic summary successfully with multiple subjects")
    void shouldGetAcademicSummarySuccessfullyWithMultipleSubjects() {
        // Given
        when(subjectRepository.findByStudentId("student123")).thenReturn(testSubjects);

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student123");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Mathematics", result.get(0).getSubjectName());
        assertEquals("Physics", result.get(1).getSubjectName());
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should get empty list when student has no subjects")
    void shouldGetEmptyListWhenStudentHasNoSubjects() {
        // Given
        when(subjectRepository.findByStudentId("student456")).thenReturn(List.of());

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student456");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentId("student456");
    }

    @Test
    @DisplayName("Should get single subject when student has only one subject")
    void shouldGetSingleSubjectWhenStudentHasOnlyOneSubject() {
        // Given
        List<Subject> singleSubjectList = List.of(testSubject1);
        when(subjectRepository.findByStudentId("student123")).thenReturn(singleSubjectList);

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getSubjectName());
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should get academic summary for any valid student ID")
    void shouldGetAcademicSummaryForAnyValidStudentId() {
        // Given
        String studentId = "student789";
        when(subjectRepository.findByStudentId(studentId)).thenReturn(List.of());

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary(studentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    @DisplayName("Should return the same list instance from repository")
    void shouldReturnSameListInstanceFromRepository() {
        // Given
        when(subjectRepository.findByStudentId("student123")).thenReturn(testSubjects);

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student123");

        // Then
        assertSame(testSubjects, result);
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should handle null student ID gracefully")
    void shouldHandleNullStudentIdGracefully() {
        // Given
        when(subjectRepository.findByStudentId(null)).thenReturn(List.of());

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentId(null);
    }

    @Test
    @DisplayName("Should handle empty student ID gracefully")
    void shouldHandleEmptyStudentIdGracefully() {
        // Given
        String emptyStudentId = "";
        when(subjectRepository.findByStudentId(emptyStudentId)).thenReturn(List.of());

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary(emptyStudentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentId(emptyStudentId);
    }

    @Test
    @DisplayName("Should call repository exactly once per request")
    void shouldCallRepositoryExactlyOncePerRequest() {
        // Given
        when(subjectRepository.findByStudentId("student123")).thenReturn(testSubjects);

        // When
        getAcademicSummaryUseCase.getSummary("student123");

        // Then
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should handle subjects with evaluation cuts")
    void shouldHandleSubjectsEvaluationCuts() {
        // Given
        Subject subjectWithCuts = Subject.builder()
            .id(3L)
            .studentId("student123")
            .subjectName("Chemistry")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Brown")
            .evaluationCuts(List.of(
                com.aibert.dosw.domain.model.EvaluationCut.builder()
                    .id(1L)
                    .cutName("Corte 1")
                    .cutPercentage(50.0)
                    .grade(null)
                    .build(),
                com.aibert.dosw.domain.model.EvaluationCut.builder()
                    .id(2L)
                    .cutName("Corte 2")
                    .cutPercentage(50.0)
                    .grade(null)
                    .build()
            ))
            .build();
        
        List<Subject> subjectsWithCuts = List.of(subjectWithCuts);
        when(subjectRepository.findByStudentId("student123")).thenReturn(subjectsWithCuts);

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chemistry", result.get(0).getSubjectName());
        assertEquals(2, result.get(0).getEvaluationCuts().size());
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should preserve subject details in returned summary")
    void shouldPreserveSubjectDetailsInReturnedSummary() {
        // Given
        when(subjectRepository.findByStudentId("student123")).thenReturn(testSubjects);

        // When
        List<Subject> result = getAcademicSummaryUseCase.getSummary("student123");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Subject firstSubject = result.get(0);
        assertEquals(1L, firstSubject.getId());
        assertEquals("student123", firstSubject.getStudentId());
        assertEquals("Mathematics", firstSubject.getSubjectName());
        assertEquals("2025-1", firstSubject.getSemester());
        assertEquals(4, firstSubject.getCredits());
        assertEquals("Dr. Smith", firstSubject.getTeacherName());
        
        Subject secondSubject = result.get(1);
        assertEquals(2L, secondSubject.getId());
        assertEquals("student123", secondSubject.getStudentId());
        assertEquals("Physics", secondSubject.getSubjectName());
        assertEquals("2025-1", secondSubject.getSemester());
        assertEquals(3, secondSubject.getCredits());
        assertEquals("Dr. Johnson", secondSubject.getTeacherName());
        
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }
}
