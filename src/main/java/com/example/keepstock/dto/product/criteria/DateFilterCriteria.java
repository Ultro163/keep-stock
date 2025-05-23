package com.example.keepstock.dto.product.criteria;

import com.example.keepstock.service.search.strategy.OffsetDateTimePredicateStrategy;
import com.example.keepstock.service.search.strategy.PredicateStrategy;
import com.example.keepstock.util.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class DateFilterCriteria implements FilterCriteriaDto<OffsetDateTime> {
    private final String field;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private final OffsetDateTime value;

    private final FilterOperation operation;

    private static final PredicateStrategy<OffsetDateTime> predicateStrategy = new OffsetDateTimePredicateStrategy();

    @JsonCreator
    public DateFilterCriteria(String field, String value, FilterOperation operation) {
        this.field = field;
        this.value = parseDate(value);
        this.operation = operation;
    }

    private static OffsetDateTime parseDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return OffsetDateTime.of(
                java.time.LocalDateTime.parse(value, formatter),
                ZoneOffset.UTC
        );
    }

    @Override
    public PredicateStrategy<OffsetDateTime> getPredicateStrategy() {
        return predicateStrategy;
    }
}