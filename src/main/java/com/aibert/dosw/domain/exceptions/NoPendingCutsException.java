package com.aibert.dosw.domain.exceptions;

public class NoPendingCutsException extends RuntimeException {

    public NoPendingCutsException(Long subjectId) {
        super("La materia con id " + subjectId + " no tiene cortes pendientes por calificar");
    }
}
