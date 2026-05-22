package com.aibert.dosw.domain.exceptions;

public class ScheduleHoursExceedDayException extends RuntimeException {

    public ScheduleHoursExceedDayException(double total) {
        super(String.format(
                "La suma total de horas (%.1f) supera las 24 horas disponibles en el día",
                total));
    }
}
