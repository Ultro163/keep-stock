package com.example.keepstock.service;

import com.example.keepstock.dto.product.DateFilterCriteria;
import com.example.keepstock.dto.product.FilterCriteriaDto;
import com.example.keepstock.dto.product.FilterOperation;
import com.example.keepstock.dto.product.NumberFilterCriteria;
import com.example.keepstock.dto.product.StringFilterCriteria;
import com.example.keepstock.model.Product;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class ProductSpecification implements Specification<Product> {

    private final transient List<FilterCriteriaDto> filters;

    public ProductSpecification(List<FilterCriteriaDto> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Product> root,
                                 CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        for (FilterCriteriaDto filter : filters) {
            predicate = criteriaBuilder.and(predicate, createPredicate(filter, root, criteriaBuilder));
        }

        return predicate;
    }

    @SuppressWarnings("unchecked")
    private static Predicate createPredicate(FilterCriteriaDto filter,
                                             Root<Product> root,
                                             CriteriaBuilder criteriaBuilder) {
        Path<?> path = root.get(filter.getField());

        return switch (filter) {
            case StringFilterCriteria stringFilter ->
                    handleStringFilter(stringFilter.getOperation(), (Path<String>) path,
                            criteriaBuilder, stringFilter.getValue());
            case DateFilterCriteria dateFilter ->
                    handleDateFilter(dateFilter.getOperation(), (Path<OffsetDateTime>) path,
                            criteriaBuilder, dateFilter.getValue());
            case NumberFilterCriteria numberFilter ->
                    handleNumberFilter(numberFilter.getOperation(), (Path<Number>) path,
                            criteriaBuilder, numberFilter.getValue());
            default ->
                    throw new IllegalArgumentException("Unsupported filter type: " + filter.getClass().getSimpleName());
        };
    }

    private static Predicate handleStringFilter(FilterOperation operation,
                                                Path<String> path,
                                                CriteriaBuilder criteriaBuilder, String value) {
        return switch (operation) {
            case EQUAL -> criteriaBuilder.equal(path, value);
            case LIKE -> criteriaBuilder.like(path, "%" + value + "%");
            default -> throw new IllegalArgumentException("Unsupported string operation: " + operation);
        };
    }

    private static Predicate handleDateFilter(FilterOperation operation,
                                              Path<OffsetDateTime> path,
                                              CriteriaBuilder criteriaBuilder,
                                              OffsetDateTime value) {
        return switch (operation) {
            case GREATER_THAN_OR_EQ -> criteriaBuilder.greaterThanOrEqualTo(path, value);
            case LESS_THAN_OR_EQ -> criteriaBuilder.lessThanOrEqualTo(path, value);
            case EQUAL -> criteriaBuilder.equal(path, value);
            default -> throw new IllegalArgumentException("Unsupported date operation: " + operation);
        };
    }

    private static Predicate handleNumberFilter(FilterOperation operation,
                                                Path<? extends Number> path,
                                                CriteriaBuilder criteriaBuilder,
                                                Number value) {
        return switch (operation) {
            case GREATER_THAN_OR_EQ ->
                    criteriaBuilder.ge(path.as(BigDecimal.class), BigDecimal.valueOf(value.doubleValue()));
            case LESS_THAN_OR_EQ ->
                    criteriaBuilder.le(path.as(BigDecimal.class), BigDecimal.valueOf(value.doubleValue()));
            case EQUAL -> criteriaBuilder.equal(path, value);
            default -> throw new IllegalArgumentException("Unsupported number operation: " + operation);
        };
    }
}