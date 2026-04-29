package com.aibert.dosw.domain.exceptions;

public class CutCapacityExceededException extends RuntimeException {

    public CutCapacityExceededException(Double currentTotal, Double newPercentage) {
        super("No es posible agregar la actividad: la suma de porcentajes del corte quedaría en "
                + (currentTotal + newPercentage) + "% (máximo 100%)");
    }
}
