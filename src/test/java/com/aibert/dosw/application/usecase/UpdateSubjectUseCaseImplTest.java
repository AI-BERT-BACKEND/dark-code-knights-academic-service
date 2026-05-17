package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateSubjectUseCaseImpl Tests")
class UpdateSubjectUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private UpdateSubjectUseCaseImpl updateSubjectUseCase;

    private Subject existingSubject;
    private Subject updatedSubject;
    private List<EvaluationCut> validEvaluationCuts;

    @BeforeEach
    void setUp() {
        validEvaluationCuts = List.of(
            new EvaluationCut(null, "Corte 1", 30.0, null),
            new EvaluationCut(null, "Corte 2", 40.0, null),
            new EvaluationCut(null, "Corte 3", 30.0, null)
        );
        
        existingSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(validEvaluationCuts)
            .build();

        updatedSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Advanced Mathematics")
            .semester("2025-1")
            .schedule("Martes 10:00 - 12:00")
            .credits(5)
            .teacherName("Dr. Johnson")
            .evaluationCuts(validEvaluationCuts)
            .build();
    }

    @Test
    @DisplayName("Should update subject successfully with valid data")
    void shouldUpdateSubjectSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        Subject result = updateSubjectUseCase.update(1L, updatedSubject);

        // Then
        assertNotNull(result);
        assertEquals("Advanced Mathematics", result.getSubjectName());
        assertEquals(5, result.getCredits());
        assertEquals("Dr. Johnson", result.getTeacherName());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> updateSubjectUseCase.update(1L, updatedSubject)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw DuplicateSubjectException when updating to duplicate name in same semester")
    void shouldThrowDuplicateSubjectExceptionWhenUpdatingToDuplicateNameInSameSemester() {
        // Given
        Subject duplicateSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Physics")
            .semester("2025-1")
            .schedule("Miercoles 14:00 - 16:00")
            .credits(4)
            .teacherName("Dr. Brown")
            .evaluationCuts(validEvaluationCuts)
            .build();
            
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Physics"), eq("2025-1"))).thenReturn(true);

        // When & Then
        DuplicateSubjectException exception = assertThrows(
            DuplicateSubjectException.class,
            () -> updateSubjectUseCase.update(1L, duplicateSubject)
        );
        
        assertTrue(exception.getMessage().contains("Physics"));
        assertTrue(exception.getMessage().contains("2025-1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Physics", "2025-1");
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should update successfully when updating to same name (should not throw duplicate)")
    void shouldUpdateSuccessfullyWhenUpdatingToSameName() {
        // Given
        Subject sameNameSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .credits(5)
            .teacherName("Dr. Johnson")
            .evaluationCuts(validEvaluationCuts)
            .build();
            
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(sameNameSubject);

        // When
        Subject result = updateSubjectUseCase.update(1L, sameNameSubject);

        // Then
        assertNotNull(result);
        assertEquals("Mathematics", result.getSubjectName());
        assertEquals(5, result.getCredits());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should update successfully when changing semester")
    void shouldUpdateSuccessfullyWhenChangingSemester() {
        // Given
        Subject differentSemesterSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Advanced Mathematics")
            .semester("2025-2")
            .schedule("Jueves 07:00 - 09:00")
            .credits(5)
            .teacherName("Dr. Johnson")
            .evaluationCuts(validEvaluationCuts)
            .build();
            
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Advanced Mathematics"), eq("2025-2"))).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(differentSemesterSubject);

        // When
        Subject result = updateSubjectUseCase.update(1L, differentSemesterSubject);

        // Then
        assertNotNull(result);
        assertEquals("Advanced Mathematics", result.getSubjectName());
        assertEquals("2025-2", result.getSemester());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Advanced Mathematics", "2025-2");
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is null")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsListIsNull() {
        // Given
        Subject subjectWithNullCuts = new Subject(
            updatedSubject.getId(),
            updatedSubject.getStudentId(),
            updatedSubject.getSubjectName(),
            updatedSubject.getCredits(),
            updatedSubject.getTeacherName(),
            updatedSubject.getSemester(),
            updatedSubject.getSchedule(),
            null
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> updateSubjectUseCase.update(1L, subjectWithNullCuts)
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is empty")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsListIsEmpty() {
        // Given
        Subject subjectWithEmptyCuts = new Subject(
            updatedSubject.getId(),
            updatedSubject.getStudentId(),
            updatedSubject.getSubjectName(),
            updatedSubject.getCredits(),
            updatedSubject.getTeacherName(),
            updatedSubject.getSemester(),
            updatedSubject.getSchedule(),
            List.of()
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> updateSubjectUseCase.update(1L, subjectWithEmptyCuts)
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is less than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsLessThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            new EvaluationCut(null, "Corte 1", 30.0, null),
            new EvaluationCut(null, "Corte 2", 40.0, null)
        );
        Subject subjectWithInvalidCuts = new Subject(
            updatedSubject.getId(),
            updatedSubject.getStudentId(),
            updatedSubject.getSubjectName(),
            updatedSubject.getCredits(),
            updatedSubject.getTeacherName(),
            updatedSubject.getSemester(),
            updatedSubject.getSchedule(),
            invalidCuts
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> updateSubjectUseCase.update(1L, subjectWithInvalidCuts)
        );

        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("70.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is greater than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsGreaterThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            new EvaluationCut(null, "Corte 1", 60.0, null),
            new EvaluationCut(null, "Corte 2", 50.0, null)
        );
        Subject subjectWithInvalidCuts = new Subject(
            updatedSubject.getId(),
            updatedSubject.getStudentId(),
            updatedSubject.getSubjectName(),
            updatedSubject.getCredits(),
            updatedSubject.getTeacherName(),
            updatedSubject.getSemester(),
            updatedSubject.getSchedule(),
            invalidCuts
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> updateSubjectUseCase.update(1L, subjectWithInvalidCuts)
        );

        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("110.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should update successfully when percentage sum is exactly 100")
    void shouldUpdateSuccessfullyWhenPercentageSumIsExactly100() {
        // Given
        List<EvaluationCut> exactCuts = List.of(
            new EvaluationCut(null, "Corte 1", 50.0, null),
            new EvaluationCut(null, "Corte 2", 50.0, null)
        );
        Subject subjectWithExactCuts = new Subject(
            updatedSubject.getId(),
            updatedSubject.getStudentId(),
            updatedSubject.getSubjectName(),
            updatedSubject.getCredits(),
            updatedSubject.getTeacherName(),
            updatedSubject.getSemester(),
            updatedSubject.getSchedule(),
            exactCuts
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithExactCuts);

        // When
        Subject result = updateSubjectUseCase.update(1L, subjectWithExactCuts);

        // Then
        assertNotNull(result);
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should preserve original student ID and ID when updating")
    void shouldPreserveOriginalStudentIdAndIdWhenUpdating() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(existingSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        Subject result = updateSubjectUseCase.update(1L, updatedSubject);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("student123", result.getStudentId());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }
}
