package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.EvaluationCut;

import java.util.List;

public interface ConfigureEvaluationStructureUseCase {

    List<EvaluationCut> configure(Long subjectId, List<EvaluationCut> evaluationCuts);

    List<EvaluationCut> getStructure(Long subjectId);
}
