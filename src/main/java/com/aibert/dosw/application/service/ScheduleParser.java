package com.aibert.dosw.application.service;

import com.aibert.dosw.domain.model.ScheduleSlot;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a free-form schedule string into a list of {@link ScheduleSlot}s.
 *
 * <p>Expected slot format: {@code DÍA HH:MM-HH:MM}
 * <br>Example: {@code "LUNES 08:00-10:00, MIERCOLES 14:00-16:00"}
 *
 * <p>Recognised day names (case-insensitive, accent-tolerant):
 * LUNES, MARTES, MIERCOLES/MIÉRCOLES, JUEVES, VIERNES, SABADO/SÁBADO.
 *
 * <p>Slots that do not match the pattern are silently skipped, so existing
 * subjects with free-form schedules cause no errors.
 */
@Service
public class ScheduleParser {

    private static final Pattern SLOT_PATTERN = Pattern.compile(
            "(?i)(LUNES|MARTES|MI[EÉ]RCOLES|JUEVES|VIERNES|S[AÁ]BADO)\\s+(\\d{2}:\\d{2})-(\\d{2}:\\d{2})"
    );

    /**
     * Parses {@code schedule} and returns every recognisable time slot.
     * Never returns {@code null} — an unrecognisable string yields an empty list.
     */
    public List<ScheduleSlot> parse(String schedule) {
        List<ScheduleSlot> slots = new ArrayList<>();
        if (schedule == null || schedule.isBlank()) {
            return slots;
        }

        Matcher matcher = SLOT_PATTERN.matcher(schedule);
        while (matcher.find()) {
            try {
                String day = normaliseDay(matcher.group(1));
                LocalTime start = LocalTime.parse(matcher.group(2));
                LocalTime end = LocalTime.parse(matcher.group(3));
                if (start.isBefore(end)) {
                    slots.add(ScheduleSlot.builder()
                            .day(day)
                            .startTime(start)
                            .endTime(end)
                            .build());
                }
            } catch (DateTimeParseException ignored) {
                // skip malformed time — cannot happen given the regex, but guard anyway
            }
        }
        return slots;
    }

    /** Normalises accented/lowercase day names to their canonical uppercase form. */
    private String normaliseDay(String raw) {
        return switch (raw.toUpperCase()
                .replace('É', 'E')
                .replace('Á', 'A')) {
            case "LUNES" -> "LUNES";
            case "MARTES" -> "MARTES";
            case "MIERCOLES" -> "MIERCOLES";
            case "JUEVES" -> "JUEVES";
            case "VIERNES" -> "VIERNES";
            case "SABADO" -> "SABADO";
            default -> raw.toUpperCase();
        };
    }
}
