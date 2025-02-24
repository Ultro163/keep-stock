package com.example.keepstock.dto.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum FilterOperation {
    EQUAL("=", "eq"),
    GREATER_THAN_OR_EQ(">=", "gte", "greaterOrEqual"),
    LESS_THAN_OR_EQ("<=", "lte", "lessOrEqual"),
    LIKE("~", "contains", "like");

    private static final Map<String, FilterOperation> OPERATION_MAP = new HashMap<>();

    static {
        for (FilterOperation op : values()) {
            for (String alias : op.aliases) {
                OPERATION_MAP.put(alias.toLowerCase(), op);
            }
            OPERATION_MAP.put(op.symbol, op);
            OPERATION_MAP.put(op.name().toLowerCase(), op);
        }
    }

    private final String symbol;
    private final String[] aliases;

    FilterOperation(String symbol, String... aliases) {
        this.symbol = symbol;
        this.aliases = aliases;
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
        FilterOperation operation = OPERATION_MAP.get(value.toLowerCase());
        if (operation == null) {
            throw new IllegalArgumentException("Unsupported operation: " + value);
        }
        return operation;
    }
}

