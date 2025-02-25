package com.example.keepstock.service.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface PredicateStrategy<T> {
    Predicate getEqPattern(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getLeftLimitPattern(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getRightLimitPattern(Expression<T> expression, T value, CriteriaBuilder cb);

    Predicate getLikePattern(Expression<T> expression, T value, CriteriaBuilder cb);
}
