package com.example.keepstock.dto.product.criteria;

import com.example.keepstock.service.search.strategy.PredicateStrategy;
import com.example.keepstock.service.search.strategy.StringPredicateStrategy;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class StringFilterCriteria implements FilterCriteriaDto<String> {

    private final String field;
    private final String value;
    private final FilterOperation operation;
    private static final PredicateStrategy<String> predicateStrategy = new StringPredicateStrategy();

    @JsonCreator
    public StringFilterCriteria(
            @JsonProperty("field") String field,
            @JsonProperty("value") String value,
            @JsonProperty("operation") FilterOperation operation) {
        this.field = field;
        this.value = value;
        this.operation = operation;
    }

    @Override
    public PredicateStrategy<String> getPredicateStrategy() {
        return predicateStrategy;
    }
}