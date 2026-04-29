package com.aibert.dosw.domain.exceptions;

public class GradeNotFoundException extends RuntimeException {

    public GradeNotFoundException(Long id) {
        super("Nota con id " + id + " no encontrada");
    }
}
