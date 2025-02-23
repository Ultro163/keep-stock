package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StringFilterCriteria implements FilterCriteriaDto {
    private final String field;
    private final String value;
    private final FilterOperation operation;
}