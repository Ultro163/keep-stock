package com.example.keepstock.dto.product;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        visible = true,
        include = JsonTypeInfo.As.PROPERTY,
        property = "field"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringFilterCriteria.class, name = "name"),
        @JsonSubTypes.Type(value = DateFilterCriteria.class, name = "createdAt"),
        @JsonSubTypes.Type(value = BigDecimalFilterCriteria.class, name = "price"),
})
public interface FilterCriteriaDto<T> {
    String getField();

    T getValue();

    FilterOperation getOperation();
}