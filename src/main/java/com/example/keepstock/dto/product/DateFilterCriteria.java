package com.example.keepstock.dto.product;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final OffsetDateTime value;

    private final FilterOperation operation;

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
}
