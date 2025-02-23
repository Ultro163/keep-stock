package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class DateFilterCriteria implements FilterCriteriaDto {
    private final String field;
    private final OffsetDateTime value;
    private final FilterOperation operation;
}