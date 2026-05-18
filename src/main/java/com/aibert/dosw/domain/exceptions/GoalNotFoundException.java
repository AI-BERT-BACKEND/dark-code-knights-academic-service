package com.aibert.dosw.domain.exceptions;

public class GoalNotFoundException extends RuntimeException {

    public GoalNotFoundException(Long subjectId) {
        super("No existe una meta académica para la materia con id " + subjectId);
    }
}
