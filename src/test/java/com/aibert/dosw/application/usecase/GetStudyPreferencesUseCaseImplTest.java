package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.StudyPreferencesNotFoundException;
import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.model.StudyTime;
import com.aibert.dosw.domain.ports.out.StudyPreferencesRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetStudyPreferencesUseCaseImpl Tests")
class GetStudyPreferencesUseCaseImplTest {

    @Mock
    private StudyPreferencesRepositoryPort preferencesRepository;

    @InjectMocks
    private GetStudyPreferencesUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";

    private StudyPreferences buildPreferences(String studentId) {
        return StudyPreferences.builder()
                .id(1L)
                .studentId(studentId)
                .preferredStudyTime(StudyTime.AFTERNOON)
                .preferredStudyMethod(StudyMethod.MIXED)
                .weeklyStudyHoursGoal(15)
                .preferredStudyLocation("Biblioteca")
                .notificationsEnabled(true)
                .build();
    }

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return preferences when they exist")
    void shouldReturnPreferencesWhenExist() {
        StudyPreferences expected = buildPreferences(STUDENT_ID);
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(expected));

        StudyPreferences result = useCase.get(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(StudyTime.AFTERNOON, result.getPreferredStudyTime());
        assertEquals(StudyMethod.MIXED, result.getPreferredStudyMethod());
        assertEquals(15, result.getWeeklyStudyHoursGoal());
        assertEquals("Biblioteca", result.getPreferredStudyLocation());
        assertTrue(result.isNotificationsEnabled());
    }

    @Test
    @DisplayName("Should call findByStudentId with exact studentId")
    void shouldCallRepositoryWithCorrectStudentId() {
        when(preferencesRepository.findByStudentId(STUDENT_ID))
                .thenReturn(Optional.of(buildPreferences(STUDENT_ID)));

        useCase.get(STUDENT_ID);

        verify(preferencesRepository, times(1)).findByStudentId(STUDENT_ID);
        verify(preferencesRepository, never()).findByStudentId("other");
    }

    @Test
    @DisplayName("Should return preferences with notificationsEnabled=false")
    void shouldReturnPreferencesWithNotificationsDisabled() {
        StudyPreferences prefs = StudyPreferences.builder()
                .id(5L).studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.NIGHT)
                .preferredStudyMethod(StudyMethod.INDIVIDUAL)
                .weeklyStudyHoursGoal(3)
                .preferredStudyLocation("Casa")
                .notificationsEnabled(false)
                .build();

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(prefs));

        StudyPreferences result = useCase.get(STUDENT_ID);

        assertFalse(result.isNotificationsEnabled());
    }

    // ─── Error paths ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw StudyPreferencesNotFoundException when no preferences found")
    void shouldThrowWhenPreferencesNotFound() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

        StudyPreferencesNotFoundException ex = assertThrows(
                StudyPreferencesNotFoundException.class,
                () -> useCase.get(STUDENT_ID));

        assertTrue(ex.getMessage().contains(STUDENT_ID));
    }

    @Test
    @DisplayName("Should throw exception with the correct studentId in message")
    void shouldIncludeStudentIdInExceptionMessage() {
        String specificId = "specific-student-999";
        when(preferencesRepository.findByStudentId(specificId)).thenReturn(Optional.empty());

        StudyPreferencesNotFoundException ex = assertThrows(
                StudyPreferencesNotFoundException.class,
                () -> useCase.get(specificId));

        assertTrue(ex.getMessage().contains(specificId));
    }

    @Test
    @DisplayName("Should not call repository more than once")
    void shouldCallRepositoryExactlyOnce() {
        when(preferencesRepository.findByStudentId(STUDENT_ID))
                .thenReturn(Optional.of(buildPreferences(STUDENT_ID)));

        useCase.get(STUDENT_ID);

        verify(preferencesRepository, times(1)).findByStudentId(any());
    }
}
