package com.aibert.dosw.domain.exceptions;

public class EvaluationStructureLockedException extends RuntimeException {

    public EvaluationStructureLockedException(Long subjectId) {
        super("La estructura de evaluación de la materia " + subjectId
                + " no puede editarse porque ya tiene notas registradas");
    }
}
