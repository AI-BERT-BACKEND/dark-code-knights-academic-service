package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    private Long id;
    private Long cutId;
    private String activityName;
    private Double gradeValue;
    private Double percentage;
}
