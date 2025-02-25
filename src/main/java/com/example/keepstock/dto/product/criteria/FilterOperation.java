package com.example.keepstock.dto.product.criteria;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterOperation {
    EQUAL("="),
    GREATER_THAN_OR_EQ(">="),
    LESS_THAN_OR_EQ("<="),
    LIKE("~");

    private final String symbol;

    FilterOperation(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static FilterOperation fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Filter operation cannot be null");
        }

        for (FilterOperation op : values()) {
            if (op.symbol.equals(value) || op.name().equalsIgnoreCase(value)) {
                return op;
            }
        }

        throw new IllegalArgumentException("Unsupported operation: " + value);
    }
}
