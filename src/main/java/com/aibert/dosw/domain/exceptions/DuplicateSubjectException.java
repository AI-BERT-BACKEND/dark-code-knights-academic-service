package com.aibert.dosw.domain.exceptions;

public class DuplicateSubjectException extends RuntimeException {

    public DuplicateSubjectException(String subjectName, String semester) {
        super("Ya existe una materia con el nombre '" + subjectName + "' en el semestre " + semester);
    }
}
