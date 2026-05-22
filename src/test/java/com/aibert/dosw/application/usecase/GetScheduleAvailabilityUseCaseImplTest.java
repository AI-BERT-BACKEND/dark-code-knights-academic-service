package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.ScheduleAvailabilityNotFoundException;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.out.ScheduleAvailabilityRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetScheduleAvailabilityUseCaseImpl Tests")
class GetScheduleAvailabilityUseCaseImplTest {

    @Mock
    private ScheduleAvailabilityRepositoryPort repository;

    @InjectMocks
    private GetScheduleAvailabilityUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";

    private ScheduleAvailability buildAvailability() {
        return ScheduleAvailability.builder()
                .id(1L)
                .studentId(STUDENT_ID)
                .freeTimeHours(4.0)
                .restHours(8.0)
                .personalTimeHours(2.0)
                .socialTimeHours(2.0)
                .maxStudyHoursPerDay(6.0)
                .build();
    }

    @Test
    @DisplayName("Should return availability when it exists")
    void shouldReturnAvailabilityWhenExists() {
        ScheduleAvailability expected = buildAvailability();
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(expected));

        ScheduleAvailability result = useCase.get(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(4.0, result.getFreeTimeHours());
        assertEquals(8.0, result.getRestHours());
        assertEquals(2.0, result.getPersonalTimeHours());
        assertEquals(2.0, result.getSocialTimeHours());
        assertEquals(6.0, result.getMaxStudyHoursPerDay());
    }

    @Test
    @DisplayName("Should throw ScheduleAvailabilityNotFoundException when not found")
    void shouldThrowWhenNotFound() {
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

        ScheduleAvailabilityNotFoundException ex = assertThrows(
                ScheduleAvailabilityNotFoundException.class,
                () -> useCase.get(STUDENT_ID));

        assertTrue(ex.getMessage().contains(STUDENT_ID));
    }

    @Test
    @DisplayName("Should throw exception with the correct studentId in message")
    void shouldIncludeStudentIdInExceptionMessage() {
        String specificId = "specific-999";
        when(repository.findByStudentId(specificId)).thenReturn(Optional.empty());

        ScheduleAvailabilityNotFoundException ex = assertThrows(
                ScheduleAvailabilityNotFoundException.class,
                () -> useCase.get(specificId));

        assertTrue(ex.getMessage().contains(specificId));
    }

    @Test
    @DisplayName("Should call repository with exact studentId")
    void shouldCallRepositoryWithCorrectStudentId() {
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(buildAvailability()));

        useCase.get(STUDENT_ID);

        verify(repository, times(1)).findByStudentId(STUDENT_ID);
        verify(repository, never()).findByStudentId("other");
    }

    @Test
    @DisplayName("Should call repository exactly once")
    void shouldCallRepositoryExactlyOnce() {
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(buildAvailability()));

        useCase.get(STUDENT_ID);

        verify(repository, times(1)).findByStudentId(any());
    }

    @Test
    @DisplayName("Should return availability with null optional fields")
    void shouldReturnAvailabilityWithNullFields() {
        ScheduleAvailability partial = ScheduleAvailability.builder()
                .id(2L).studentId(STUDENT_ID)
                .freeTimeHours(null).restHours(8.0)
                .personalTimeHours(null).socialTimeHours(null)
                .maxStudyHoursPerDay(null)
                .build();

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(partial));

        ScheduleAvailability result = useCase.get(STUDENT_ID);

        assertEquals(8.0, result.getRestHours());
    }
}
