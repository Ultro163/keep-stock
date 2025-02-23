package com.example.keepstock.config.jackson;

import com.example.keepstock.dto.product.DateFilterCriteria;
import com.example.keepstock.dto.product.FilterCriteriaDto;
import com.example.keepstock.dto.product.FilterOperation;
import com.example.keepstock.dto.product.NumberFilterCriteria;
import com.example.keepstock.dto.product.StringFilterCriteria;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class FilterCriteriaDeserializer extends JsonDeserializer<FilterCriteriaDto> {

    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public FilterCriteriaDto deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (!node.has("field") || !node.has("operation") || !node.has("value")) {
            throw new IOException("Missing required fields in JSON");
        }

        String field = node.get("field").asText();
        String operationStr = node.get("operation").asText();
        FilterOperation operation = FilterOperation.fromString(operationStr);
        JsonNode valueNode = node.get("value");

        // Если значение null
        if (valueNode.isNull()) {
            throw new IOException("Value cannot be null for field: " + field);
        }

        // Обработка чисел
        if (valueNode.isNumber()) {
            return new NumberFilterCriteria(field, BigDecimal.valueOf(valueNode.asDouble()), operation);
        }

        String valueStr = valueNode.asText().trim();

        try {
            OffsetDateTime dateValue = OffsetDateTime.of(
                    LocalDateTime.parse(valueStr, CUSTOM_DATE_FORMATTER), ZoneOffset.UTC);
            return new DateFilterCriteria(field, dateValue, operation);
        } catch (DateTimeParseException ignored) {
            return new StringFilterCriteria(field, valueStr, operation);
        }
    }
}
