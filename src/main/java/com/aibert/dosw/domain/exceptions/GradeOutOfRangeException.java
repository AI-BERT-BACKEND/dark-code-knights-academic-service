package com.aibert.dosw.domain.exceptions;

public class GradeOutOfRangeException extends RuntimeException {

    public GradeOutOfRangeException(Double value) {
        super("La nota " + value + " está fuera del rango permitido (0.0 a 5.0)");
    }
}
