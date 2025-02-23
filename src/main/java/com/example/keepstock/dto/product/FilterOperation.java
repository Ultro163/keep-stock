package com.example.keepstock.dto.product;

public enum FilterOperation {
    EQUAL("="),
    GREATER_THAN_OR_EQ(">="),
    LESS_THAN_OR_EQ("<="),
    LIKE("~");

    private final String symbol;

    FilterOperation(String symbol) {
        this.symbol = symbol;
    }

    public static FilterOperation fromString(String value) {
        for (FilterOperation op : values()) {
            if (op.name().equalsIgnoreCase(value) || op.symbol.equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unsupported operation: " + value);
    }
}