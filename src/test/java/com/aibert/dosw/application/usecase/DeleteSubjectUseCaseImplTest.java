package com.aibert.dosw.application.usecase;

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
@DisplayName("DeleteSubjectUseCaseImpl Tests")
class DeleteSubjectUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private DeleteSubjectUseCaseImpl deleteSubjectUseCase;

    private Subject existingSubject;

    @BeforeEach
    void setUp() {
        existingSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of())
            .build();
    }

    private static final String STUDENT_ID = "student123";

    @Test
    @DisplayName("Should delete subject successfully when subject exists and belongs to student")
    void shouldDeleteSubjectSuccessfully() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, STUDENT_ID)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(1L, STUDENT_ID));

        // Then
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, STUDENT_ID);
        verify(subjectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotOwnedException when subject not found or wrong student")
    void shouldThrowSubjectNotOwnedExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, STUDENT_ID)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotOwnedException exception = assertThrows(
            SubjectNotOwnedException.class,
            () -> deleteSubjectUseCase.delete(1L, STUDENT_ID)
        );

        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, STUDENT_ID);
        verify(subjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should not call deleteById when subject not owned")
    void shouldNotCallDeleteByIdWhenSubjectNotOwned() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, STUDENT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubjectNotOwnedException.class, () -> deleteSubjectUseCase.delete(1L, STUDENT_ID));

        // Then
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, STUDENT_ID);
        verify(subjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should call deleteById exactly once when subject exists and is owned")
    void shouldCallDeleteByIdExactlyOnceWhenSubjectExists() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, STUDENT_ID)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When
        deleteSubjectUseCase.delete(1L, STUDENT_ID);

        // Then
        verify(subjectRepository, times(1)).deleteById(1L);
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, STUDENT_ID);
    }

    @Test
    @DisplayName("Should delete subject with any valid ID when owned by student")
    void shouldDeleteSubjectWithAnyValidId() {
        // Given
        Long testId = 999L;
        Subject testSubject = new Subject(
            testId,
            existingSubject.getStudentId(),
            existingSubject.getSubjectName(),
            existingSubject.getCredits(),
            existingSubject.getTeacherName(),
            existingSubject.getSemester(),
            existingSubject.getSchedule(),
                null,
            existingSubject.getEvaluationCuts()
        );
        when(subjectRepository.findByIdAndStudentId(testId, STUDENT_ID)).thenReturn(Optional.of(testSubject));
        doNothing().when(subjectRepository).deleteById(testId);

        // When
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(testId, STUDENT_ID));

        // Then
        verify(subjectRepository, times(1)).findByIdAndStudentId(testId, STUDENT_ID);
        verify(subjectRepository, times(1)).deleteById(testId);
    }

    @Test
    @DisplayName("Should not throw any exception when deletion is successful")
    void shouldNotThrowAnyExceptionWhenDeletionIsSuccessful() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, STUDENT_ID)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(1L, STUDENT_ID));
        verify(subjectRepository, times(1)).findByIdAndStudentId(1L, STUDENT_ID);
        verify(subjectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotOwnedException with correct message")
    void shouldThrowSubjectNotOwnedExceptionWithCorrectMessage() {
        // Given
        Long testId = 42L;
        when(subjectRepository.findByIdAndStudentId(testId, STUDENT_ID)).thenReturn(Optional.empty());

        // When
        SubjectNotOwnedException exception = assertThrows(
            SubjectNotOwnedException.class,
            () -> deleteSubjectUseCase.delete(testId, STUDENT_ID)
        );

        // Then
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(String.valueOf(testId)));
        verify(subjectRepository, times(1)).findByIdAndStudentId(testId, STUDENT_ID);
        verify(subjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw SubjectNotOwnedException when student ID does not match")
    void shouldThrowSubjectNotOwnedExceptionWhenStudentIdDoesNotMatch() {
        // Given
        when(subjectRepository.findByIdAndStudentId(1L, "otherStudent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubjectNotOwnedException.class,
                () -> deleteSubjectUseCase.delete(1L, "otherStudent"));
        verify(subjectRepository, never()).deleteById(any());
    }
}
