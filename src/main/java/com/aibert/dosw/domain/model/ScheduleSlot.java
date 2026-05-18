package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlot {

    private String day;
    private LocalTime startTime;
    private LocalTime endTime;

    /**
     * Returns true when this slot and {@code other} are on the same day and
     * their time ranges overlap (touching boundaries are NOT considered a conflict).
     */
    public boolean overlapsWith(ScheduleSlot other) {
        return this.day.equals(other.day)
                && this.startTime.isBefore(other.endTime)
                && other.startTime.isBefore(this.endTime);
    }

    /** Human-readable representation, e.g. {@code "LUNES 08:00-10:00"}. */
    public String toDisplayString() {
        return day + " " + startTime + "-" + endTime;
    }
}
