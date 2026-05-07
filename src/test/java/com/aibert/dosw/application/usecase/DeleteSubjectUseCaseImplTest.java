package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
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

    @Test
    @DisplayName("Should delete subject successfully when subject exists")
    void shouldDeleteSubjectSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(1L));

        // Then
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> deleteSubjectUseCase.delete(1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should not call deleteById when subject not found")
    void shouldNotCallDeleteByIdWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubjectNotFoundException.class, () -> deleteSubjectUseCase.delete(1L));

        // Then
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should call deleteById exactly once when subject exists")
    void shouldCallDeleteByIdExactlyOnceWhenSubjectExists() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When
        deleteSubjectUseCase.delete(1L);

        // Then
        verify(subjectRepository, times(1)).deleteById(1L);
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should delete subject with any valid ID")
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
            existingSubject.getEvaluationCuts()
        );
        when(subjectRepository.findById(testId)).thenReturn(Optional.of(testSubject));
        doNothing().when(subjectRepository).deleteById(testId);

        // When
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(testId));

        // Then
        verify(subjectRepository, times(1)).findById(testId);
        verify(subjectRepository, times(1)).deleteById(testId);
    }

    @Test
    @DisplayName("Should not throw any exception when deletion is successful")
    void shouldNotThrowAnyExceptionWhenDeletionIsSuccessful() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        doNothing().when(subjectRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> deleteSubjectUseCase.delete(1L));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).deleteById(1L);
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
            () -> deleteSubjectUseCase.delete(testId)
        );

        // Then
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(String.valueOf(testId)));
        verify(subjectRepository, times(1)).findById(testId);
        verify(subjectRepository, never()).deleteById(any());
    }
}
