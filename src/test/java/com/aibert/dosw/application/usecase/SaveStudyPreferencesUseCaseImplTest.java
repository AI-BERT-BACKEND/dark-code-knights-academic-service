package com.aibert.dosw.application.usecase;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveStudyPreferencesUseCaseImpl Tests")
class SaveStudyPreferencesUseCaseImplTest {

    @Mock
    private StudyPreferencesRepositoryPort preferencesRepository;

    @InjectMocks
    private SaveStudyPreferencesUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";

    private StudyPreferences buildInput(String studentId) {
        return StudyPreferences.builder()
                .studentId(studentId)
                .preferredStudyTime(StudyTime.MORNING)
                .preferredStudyMethod(StudyMethod.INDIVIDUAL)
                .weeklyStudyHoursGoal(10)
                .preferredStudyLocation("Biblioteca")
                .notificationsEnabled(true)
                .build();
    }

    // ─── Create (no existing record) ─────────────────────────────────────────

    @Test
    @DisplayName("Should create preferences with null id when no record exists")
    void shouldCreatePreferencesWhenNoneExist() {
        StudyPreferences input = buildInput(STUDENT_ID);
        StudyPreferences saved = StudyPreferences.builder()
                .id(1L).studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.MORNING)
                .preferredStudyMethod(StudyMethod.INDIVIDUAL)
                .weeklyStudyHoursGoal(10)
                .preferredStudyLocation("Biblioteca")
                .notificationsEnabled(true)
                .build();

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenReturn(saved);

        StudyPreferences result = useCase.save(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(preferencesRepository).save(argThat(p -> p.getId() == null));
    }

    @Test
    @DisplayName("Should persist all fields on creation")
    void shouldPersistAllFieldsOnCreation() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(buildInput(STUDENT_ID));

        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(StudyTime.MORNING, result.getPreferredStudyTime());
        assertEquals(StudyMethod.INDIVIDUAL, result.getPreferredStudyMethod());
        assertEquals(10, result.getWeeklyStudyHoursGoal());
        assertEquals("Biblioteca", result.getPreferredStudyLocation());
        assertTrue(result.isNotificationsEnabled());
    }

    // ─── Update (existing record) ─────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences preserving their id")
    void shouldUpdateExistingPreferencesPreservingId() {
        StudyPreferences existing = StudyPreferences.builder()
                .id(42L).studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.AFTERNOON)
                .preferredStudyMethod(StudyMethod.GROUP)
                .weeklyStudyHoursGoal(5)
                .preferredStudyLocation("Casa")
                .notificationsEnabled(false)
                .build();

        StudyPreferences input = StudyPreferences.builder()
                .studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.NIGHT)
                .preferredStudyMethod(StudyMethod.MIXED)
                .weeklyStudyHoursGoal(20)
                .preferredStudyLocation("Cafetería")
                .notificationsEnabled(true)
                .build();

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(existing));
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(input);

        assertEquals(42L, result.getId());
        assertEquals(StudyTime.NIGHT, result.getPreferredStudyTime());
        assertEquals(StudyMethod.MIXED, result.getPreferredStudyMethod());
        assertEquals(20, result.getWeeklyStudyHoursGoal());
        assertEquals("Cafetería", result.getPreferredStudyLocation());
        assertTrue(result.isNotificationsEnabled());
    }

    @Test
    @DisplayName("Should call repository save exactly once")
    void shouldCallRepositorySaveExactlyOnce() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(STUDENT_ID));

        verify(preferencesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should call findByStudentId to check for existing record")
    void shouldCheckForExistingRecord() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(STUDENT_ID));

        verify(preferencesRepository, times(1)).findByStudentId(STUDENT_ID);
    }

    // ─── notificationsEnabled = false ────────────────────────────────────────

    @Test
    @DisplayName("Should persist notificationsEnabled=false correctly")
    void shouldPersistNotificationsDisabled() {
        StudyPreferences input = StudyPreferences.builder()
                .studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.EVENING)
                .preferredStudyMethod(StudyMethod.GROUP)
                .weeklyStudyHoursGoal(8)
                .preferredStudyLocation("Casa")
                .notificationsEnabled(false)
                .build();

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(input);

        assertFalse(result.isNotificationsEnabled());
    }

    // ─── All StudyTime + StudyMethod combinations ─────────────────────────────

    @Test
    @DisplayName("Should accept all valid StudyTime values")
    void shouldAcceptAllStudyTimeValues() {
        for (StudyTime time : StudyTime.values()) {
            StudyPreferences input = StudyPreferences.builder()
                    .studentId(STUDENT_ID).preferredStudyTime(time)
                    .preferredStudyMethod(StudyMethod.INDIVIDUAL)
                    .weeklyStudyHoursGoal(5).preferredStudyLocation("Casa")
                    .notificationsEnabled(false).build();

            when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
            when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            StudyPreferences result = useCase.save(input);
            assertEquals(time, result.getPreferredStudyTime());
        }
    }

    @Test
    @DisplayName("Should accept all valid StudyMethod values")
    void shouldAcceptAllStudyMethodValues() {
        for (StudyMethod method : StudyMethod.values()) {
            StudyPreferences input = StudyPreferences.builder()
                    .studentId(STUDENT_ID).preferredStudyTime(StudyTime.MORNING)
                    .preferredStudyMethod(method)
                    .weeklyStudyHoursGoal(5).preferredStudyLocation("Casa")
                    .notificationsEnabled(false).build();

            when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
            when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            StudyPreferences result = useCase.save(input);
            assertEquals(method, result.getPreferredStudyMethod());
        }
    }
}
