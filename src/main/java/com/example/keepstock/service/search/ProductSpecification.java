package com.example.keepstock.service.search;

import com.example.keepstock.dto.product.criteria.FilterCriteriaDto;
import com.example.keepstock.entity.Product;
import com.example.keepstock.service.search.strategy.PredicateStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductSpecification implements Specification<Product> {

    private final transient List<FilterCriteriaDto<?>> filters;

    public ProductSpecification(List<FilterCriteriaDto<?>> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate finalPredicate = cb.conjunction();

        for (FilterCriteriaDto<?> filter : filters) {
            finalPredicate = cb.and(finalPredicate, createPredicate(filter, root, cb));
        }

        return finalPredicate;
    }

    private <T> Predicate createPredicate(FilterCriteriaDto<T> filter, Root<Product> root, CriteriaBuilder cb) {
        Expression<T> expression = root.get(filter.getField());
        PredicateStrategy<T> strategy = filter.getPredicateStrategy();
        T value = filter.getValue();

        return switch (filter.getOperation()) {
            case EQUAL -> strategy.getEqPattern(expression, value, cb);
            case GREATER_THAN_OR_EQ -> strategy.getLeftLimitPattern(expression, value, cb);
            case LESS_THAN_OR_EQ -> strategy.getRightLimitPattern(expression, value, cb);
            case LIKE -> strategy.getLikePattern(expression, value, cb);
        };
    }
}