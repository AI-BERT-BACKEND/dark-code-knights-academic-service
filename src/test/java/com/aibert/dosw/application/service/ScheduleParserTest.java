package com.aibert.dosw.application.service;

import com.aibert.dosw.domain.model.ScheduleSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScheduleParser Tests")
class ScheduleParserTest {

    private ScheduleParser parser;

    @BeforeEach
    void setUp() {
        parser = new ScheduleParser();
    }

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should parse a single slot")
    void shouldParseASingleSlot() {
        List<ScheduleSlot> slots = parser.parse("LUNES 08:00-10:00");

        assertEquals(1, slots.size());
        assertEquals("LUNES", slots.get(0).getDay());
        assertEquals(LocalTime.of(8, 0), slots.get(0).getStartTime());
        assertEquals(LocalTime.of(10, 0), slots.get(0).getEndTime());
    }

    @Test
    @DisplayName("Should parse multiple slots separated by commas")
    void shouldParseMultipleSlotsSeparatedByCommas() {
        List<ScheduleSlot> slots = parser.parse("LUNES 08:00-10:00, MIERCOLES 14:00-16:00");

        assertEquals(2, slots.size());
        assertEquals("LUNES", slots.get(0).getDay());
        assertEquals("MIERCOLES", slots.get(1).getDay());
        assertEquals(LocalTime.of(14, 0), slots.get(1).getStartTime());
    }

    @Test
    @DisplayName("Should parse all supported day names")
    void shouldParseAllSupportedDayNames() {
        String schedule = "LUNES 08:00-09:00, MARTES 08:00-09:00, MIERCOLES 08:00-09:00, "
                + "JUEVES 08:00-09:00, VIERNES 08:00-09:00, SABADO 08:00-09:00";

        List<ScheduleSlot> slots = parser.parse(schedule);

        assertEquals(6, slots.size());
        List<String> days = slots.stream().map(ScheduleSlot::getDay).toList();
        assertTrue(days.containsAll(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO")));
    }

    @Test
    @DisplayName("Should parse lowercase day names")
    void shouldParseLowercaseDayNames() {
        List<ScheduleSlot> slots = parser.parse("lunes 08:00-10:00");

        assertEquals(1, slots.size());
        assertEquals("LUNES", slots.get(0).getDay());
    }

    @Test
    @DisplayName("Should parse accented day MIÉRCOLES")
    void shouldParseAccentedMiercoles() {
        List<ScheduleSlot> slots = parser.parse("MIÉRCOLES 10:00-12:00");

        assertEquals(1, slots.size());
        assertEquals("MIERCOLES", slots.get(0).getDay());
    }

    @Test
    @DisplayName("Should parse accented day SÁBADO")
    void shouldParseAccentedSabado() {
        List<ScheduleSlot> slots = parser.parse("SÁBADO 08:00-10:00");

        assertEquals(1, slots.size());
        assertEquals("SABADO", slots.get(0).getDay());
    }

    // ─── toDisplayString ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return readable display string for a slot")
    void shouldReturnReadableDisplayString() {
        List<ScheduleSlot> slots = parser.parse("VIERNES 07:00-09:00");

        assertEquals("VIERNES 07:00-09:00", slots.get(0).toDisplayString());
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return empty list for null schedule")
    void shouldReturnEmptyListForNullSchedule() {
        List<ScheduleSlot> slots = parser.parse(null);

        assertNotNull(slots);
        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for blank schedule")
    void shouldReturnEmptyListForBlankSchedule() {
        List<ScheduleSlot> slots = parser.parse("   ");

        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for unrecognised free-form schedule")
    void shouldReturnEmptyListForFreeFormSchedule() {
        List<ScheduleSlot> slots = parser.parse("Lunes y miércoles de 8 a 10");

        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("Should skip slot where start equals end (zero-duration)")
    void shouldSkipZeroDurationSlot() {
        // 08:00-08:00 — start is NOT before end, so should be skipped
        List<ScheduleSlot> slots = parser.parse("LUNES 08:00-08:00");

        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("Should skip slot where start is after end")
    void shouldSkipInvertedSlot() {
        // 10:00-08:00 — inverted, start is NOT before end
        List<ScheduleSlot> slots = parser.parse("MARTES 10:00-08:00");

        assertTrue(slots.isEmpty());
    }

    // ─── overlapsWith ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Slots on the same day with overlapping times should conflict")
    void slotsOnSameDayWithOverlapShouldConflict() {
        List<ScheduleSlot> slotsA = parser.parse("LUNES 08:00-10:00");
        List<ScheduleSlot> slotsB = parser.parse("LUNES 09:00-11:00");

        assertTrue(slotsA.get(0).overlapsWith(slotsB.get(0)));
    }

    @Test
    @DisplayName("Slots on the same day with touching boundaries should NOT conflict")
    void slotsWithTouchingBoundariesShouldNotConflict() {
        List<ScheduleSlot> slotsA = parser.parse("LUNES 08:00-10:00");
        List<ScheduleSlot> slotsB = parser.parse("LUNES 10:00-12:00");

        assertFalse(slotsA.get(0).overlapsWith(slotsB.get(0)));
    }

    @Test
    @DisplayName("Slots on different days should NOT conflict even if times overlap")
    void slotsOnDifferentDaysShouldNotConflict() {
        List<ScheduleSlot> slotsA = parser.parse("LUNES 08:00-10:00");
        List<ScheduleSlot> slotsB = parser.parse("MARTES 08:00-10:00");

        assertFalse(slotsA.get(0).overlapsWith(slotsB.get(0)));
    }

    @Test
    @DisplayName("Slot fully contained inside another should conflict")
    void slotFullyContainedInsideAnotherShouldConflict() {
        List<ScheduleSlot> slotsA = parser.parse("JUEVES 08:00-12:00");
        List<ScheduleSlot> slotsB = parser.parse("JUEVES 09:00-11:00");

        assertTrue(slotsA.get(0).overlapsWith(slotsB.get(0)));
    }
}
