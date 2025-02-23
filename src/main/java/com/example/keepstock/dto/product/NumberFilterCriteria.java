package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NumberFilterCriteria implements FilterCriteriaDto {
    private final String field;
    private final Number value;
    private final FilterOperation operation;
}