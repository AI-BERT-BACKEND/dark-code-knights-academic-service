package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.ScheduleHoursExceedDayException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveScheduleAvailabilityUseCaseImpl Tests")
class SaveScheduleAvailabilityUseCaseImplTest {

    @Mock
    private ScheduleAvailabilityRepositoryPort repository;

    @InjectMocks
    private SaveScheduleAvailabilityUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";

    private ScheduleAvailability buildInput(Double free, Double rest, Double personal,
                                            Double social, Double maxStudy) {
        return ScheduleAvailability.builder()
                .studentId(STUDENT_ID)
                .freeTimeHours(free)
                .restHours(rest)
                .personalTimeHours(personal)
                .socialTimeHours(social)
                .maxStudyHoursPerDay(maxStudy)
                .build();
    }

    // ─── Create (no existing record) ─────────────────────────────────────────

    @Test
    @DisplayName("Should create availability with null id when no record exists")
    void shouldCreateWhenNoneExist() {
        ScheduleAvailability input = buildInput(4.0, 8.0, 2.0, 2.0, 6.0);

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            ScheduleAvailability a = inv.getArgument(0);
            return ScheduleAvailability.builder()
                    .id(1L).studentId(a.getStudentId())
                    .freeTimeHours(a.getFreeTimeHours()).restHours(a.getRestHours())
                    .personalTimeHours(a.getPersonalTimeHours()).socialTimeHours(a.getSocialTimeHours())
                    .maxStudyHoursPerDay(a.getMaxStudyHoursPerDay()).build();
        });

        ScheduleAvailability result = useCase.save(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository).save(argThat(a -> a.getId() == null));
    }

    // ─── Update (existing record) ─────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing availability preserving its id")
    void shouldUpdatePreservingId() {
        ScheduleAvailability existing = ScheduleAvailability.builder()
                .id(42L).studentId(STUDENT_ID)
                .freeTimeHours(3.0).restHours(7.0).build();

        ScheduleAvailability input = buildInput(5.0, 8.0, 1.0, 1.0, 5.0);

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ScheduleAvailability result = useCase.save(input);

        assertEquals(42L, result.getId());
        assertEquals(5.0, result.getFreeTimeHours());
        assertEquals(8.0, result.getRestHours());
    }

    // ─── Validation: sum > 24 ────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw ScheduleHoursExceedDayException when sum exceeds 24")
    void shouldThrowWhenSumExceeds24() {
        ScheduleAvailability input = buildInput(8.0, 8.0, 5.0, 4.0, 4.0); // sum = 29

        ScheduleHoursExceedDayException ex = assertThrows(
                ScheduleHoursExceedDayException.class,
                () -> useCase.save(input));

        assertTrue(ex.getMessage().contains("29"));
    }

    @Test
    @DisplayName("Should accept exactly 24 hours total")
    void shouldAcceptExactly24Hours() {
        ScheduleAvailability input = buildInput(6.0, 8.0, 4.0, 3.0, 3.0); // sum = 24

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ScheduleAvailability result = useCase.save(input);

        assertNotNull(result);
    }

    // ─── Null fields (all optional) ──────────────────────────────────────────

    @Test
    @DisplayName("Should save with all fields null")
    void shouldSaveWithAllFieldsNull() {
        ScheduleAvailability input = buildInput(null, null, null, null, null);

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ScheduleAvailability result = useCase.save(input);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
    }

    @Test
    @DisplayName("Should save with only some fields provided")
    void shouldSaveWithPartialFields() {
        ScheduleAvailability input = buildInput(5.0, null, 3.0, null, 6.0); // sum = 14

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ScheduleAvailability result = useCase.save(input);

        assertEquals(5.0, result.getFreeTimeHours());
        assertEquals(6.0, result.getMaxStudyHoursPerDay());
    }

    // ─── Repository interactions ──────────────────────────────────────────────

    @Test
    @DisplayName("Should call findByStudentId exactly once")
    void shouldCallFindByStudentIdOnce() {
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(2.0, 3.0, 1.0, 1.0, 4.0));

        verify(repository, times(1)).findByStudentId(STUDENT_ID);
    }

    @Test
    @DisplayName("Should call save exactly once")
    void shouldCallSaveExactlyOnce() {
        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(2.0, 3.0, 1.0, 1.0, 4.0));

        verify(repository, times(1)).save(any());
    }

    // ─── Field persistence ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should persist all fields correctly")
    void shouldPersistAllFields() {
        ScheduleAvailability input = buildInput(3.0, 7.0, 2.0, 2.0, 5.0);

        when(repository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ScheduleAvailability result = useCase.save(input);

        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(3.0, result.getFreeTimeHours());
        assertEquals(7.0, result.getRestHours());
        assertEquals(2.0, result.getPersonalTimeHours());
        assertEquals(2.0, result.getSocialTimeHours());
        assertEquals(5.0, result.getMaxStudyHoursPerDay());
    }
}
