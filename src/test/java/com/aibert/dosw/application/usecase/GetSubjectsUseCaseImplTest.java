package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectNotOwnedException;
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
@DisplayName("GetSubjectsUseCaseImpl Tests")
class GetSubjectsUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private GetSubjectsUseCaseImpl getSubjectsUseCase;

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
    @DisplayName("Should get subject by ID successfully")
    void shouldGetSubjectByIdSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject1));

        // When
        Subject result = getSubjectsUseCase.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mathematics", result.getSubjectName());
        assertEquals("student123", result.getStudentId());
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject ID not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectIdNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> getSubjectsUseCase.getById(1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get all subjects for student with multiple subjects")
    void shouldGetAllSubjectsForStudentWithMultipleSubjects() {
        // Given
        when(subjectRepository.findByStudentId("student123")).thenReturn(testSubjects);

        // When
        List<Subject> result = getSubjectsUseCase.getAllByStudent("student123");

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
        List<Subject> result = getSubjectsUseCase.getAllByStudent("student456");

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
        List<Subject> result = getSubjectsUseCase.getAllByStudent("student123");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getSubjectName());
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should get subject by ID with any valid ID")
    void shouldGetSubjectByIdWithAnyValidId() {
        // Given
        Long testId = 999L;
        Subject testSubject = new Subject(
            testId,
            "student123",
            "Test Subject",
            3,
            "Dr. Test",
            "2025-1",
            List.of()
        );
        when(subjectRepository.findById(testId)).thenReturn(Optional.of(testSubject));

        // When
        Subject result = getSubjectsUseCase.getById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Subject", result.getSubjectName());
        verify(subjectRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should get subjects for any valid student ID")
    void shouldGetSubjectsForAnyValidStudentId() {
        // Given
        String studentId = "student789";
        when(subjectRepository.findByStudentId(studentId)).thenReturn(List.of());

        // When
        List<Subject> result = getSubjectsUseCase.getAllByStudent(studentId);

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
        List<Subject> result = getSubjectsUseCase.getAllByStudent("student123");

        // Then
        assertSame(testSubjects, result);
        verify(subjectRepository, times(1)).findByStudentId("student123");
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException with correct message")
    void shouldThrowSubjectNotFoundExceptionWithCorrectMessage() {
        // Given
        Long testId = 42L;
        when(subjectRepository.findById(testId)).thenReturn(Optional.empty());

        // When
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> getSubjectsUseCase.getById(testId)
        );

        // Then
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(String.valueOf(testId)));
        verify(subjectRepository, times(1)).findById(testId);
    }

    @Test
    @DisplayName("Should get subject by ID and student ID successfully")
    void shouldGetSubjectByIdAndStudentIdSuccessfully() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, "student123")).thenReturn(Optional.of(testSubject1));

        // When
        Subject result = getSubjectsUseCase.getByIdAndStudent(1L, "student123");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mathematics", result.getSubjectName());
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, "student123");
    }

    @Test
    @DisplayName("Should throw SubjectNotOwnedException when subject does not belong to student")
    void getByIdAndStudent_WrongStudent_ShouldThrowException() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, "otherStudent")).thenReturn(Optional.empty());

        // When & Then
        SubjectNotOwnedException exception = assertThrows(
            SubjectNotOwnedException.class,
            () -> getSubjectsUseCase.getByIdAndStudent(1L, "otherStudent")
        );

        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, "otherStudent");
    }

    @Test
    @DisplayName("Should throw SubjectNotOwnedException when subject does not exist")
    void getByIdAndStudent_SubjectNotFound_ShouldThrowException() {
        // Given
        when(subjectRepository.findByIdAndStudentId(999L, "student123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubjectNotOwnedException.class,
                () -> getSubjectsUseCase.getByIdAndStudent(999L, "student123"));
        verify(subjectRepository, times(1)).findByIdAndStudentId(999L, "student123");
    }

    @Test
    @DisplayName("Should handle null student ID gracefully")
    void shouldHandleNullStudentIdGracefully() {
        // Given
        when(subjectRepository.findByStudentId(null)).thenReturn(List.of());

        // When
        List<Subject> result = getSubjectsUseCase.getAllByStudent(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentId(null);
    }
}
