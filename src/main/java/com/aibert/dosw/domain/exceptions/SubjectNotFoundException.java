package com.aibert.dosw.domain.exceptions;

public class SubjectNotFoundException extends RuntimeException {

    public SubjectNotFoundException(Long id) {
        super("Materia con id " + id + " no encontrada");
    }
}
