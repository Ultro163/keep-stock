package com.example.keepstock.dto.product;

import com.example.keepstock.config.jackson.FilterCriteriaDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FilterCriteriaDeserializer.class)
public interface FilterCriteriaDto {
    String getField();

    Object getValue();

    FilterOperation getOperation();
}