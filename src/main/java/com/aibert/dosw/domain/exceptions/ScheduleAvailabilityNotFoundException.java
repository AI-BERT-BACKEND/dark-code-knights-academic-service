package com.aibert.dosw.domain.exceptions;

public class ScheduleAvailabilityNotFoundException extends RuntimeException {

    public ScheduleAvailabilityNotFoundException(String studentId) {
        super("No se encontró configuración de disponibilidad para el estudiante: " + studentId);
    }
}
