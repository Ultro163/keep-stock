package com.example.keepstock.dto.product.criteria;

import com.example.keepstock.service.search.strategy.BigDecimalPredicateStrategy;
import com.example.keepstock.service.search.strategy.PredicateStrategy;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BigDecimalFilterCriteria implements FilterCriteriaDto<BigDecimal> {
    private final String field;
    private final BigDecimal value;
    private final FilterOperation operation;
    private static final BigDecimalPredicateStrategy predicateStrategy = new BigDecimalPredicateStrategy();

    @JsonCreator
    public BigDecimalFilterCriteria(
            @JsonProperty("field") String field,
            @JsonProperty("value") BigDecimal value,
            @JsonProperty("operation") FilterOperation operation) {
        this.field = field;
        this.value = value;
        this.operation = operation;
    }

    @Override
    public PredicateStrategy<BigDecimal> getPredicateStrategy() {
        return predicateStrategy;
    }
}