package com.aibert.dosw.domain.exceptions;

public class StudyPreferencesNotFoundException extends RuntimeException {

    public StudyPreferencesNotFoundException(String studentId) {
        super("No se encontraron preferencias de estudio para el estudiante con id " + studentId);
    }
}
