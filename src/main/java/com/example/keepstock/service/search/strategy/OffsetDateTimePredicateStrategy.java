package com.example.keepstock.service.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class OffsetDateTimePredicateStrategy implements PredicateStrategy<OffsetDateTime> {
    @Override
    public Predicate getEqPattern(Expression<OffsetDateTime> expression, OffsetDateTime value, CriteriaBuilder cb) {
        return cb.equal(cb.function("date", LocalDateTime.class, expression), value.toLocalDate());
    }

    @Override
    public Predicate getLeftLimitPattern(Expression<OffsetDateTime> expression, OffsetDateTime value, CriteriaBuilder cb) {
        return cb.greaterThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getRightLimitPattern(Expression<OffsetDateTime> expression, OffsetDateTime value, CriteriaBuilder cb) {
        return cb.lessThanOrEqualTo(expression, value);
    }

    @Override
    public Predicate getLikePattern(Expression<OffsetDateTime> expression, OffsetDateTime value, CriteriaBuilder cb) {
        return cb.between(expression, value.minusDays(1), value.plusDays(1));
    }
}