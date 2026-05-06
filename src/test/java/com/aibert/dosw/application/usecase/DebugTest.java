package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.EvaluationCut;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugTest {

    @Test
    void debugValidation() {
        List<EvaluationCut> invalidCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(30.0)
                .build()
        );

        // Debug: Check the sum
        double total = invalidCuts.stream().mapToDouble(EvaluationCut::getCutPercentage).sum();
        System.out.println("Total percentage: " + total);
        System.out.println("Math.abs(total - 100.0): " + Math.abs(total - 100.0));
        System.out.println("Should throw: " + (Math.abs(total - 100.0) > 0.001));

        // This should be 70.0
        assertEquals(70.0, total, 0.001);
        
        // This should be true
        assertTrue(Math.abs(total - 100.0) > 0.001);
    }
}
