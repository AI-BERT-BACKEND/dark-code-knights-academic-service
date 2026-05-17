package com.aibert.dosw.domain.exceptions;

public class SubjectNotOwnedException extends RuntimeException {

    public SubjectNotOwnedException(Long subjectId) {
        super("No tienes acceso a la materia con id " + subjectId);
    }
}
