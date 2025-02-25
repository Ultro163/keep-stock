package com.example.keepstock.service.search.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class StringPredicateStrategy implements PredicateStrategy<String> {
    @Override
    public Predicate getEqPattern(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.equal(expression, value);
    }

    @Override
    public Predicate getLeftLimitPattern(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, value + "%");
    }

    @Override
    public Predicate getRightLimitPattern(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(expression, "%" + value);
    }

    @Override
    public Predicate getLikePattern(Expression<String> expression, String value, CriteriaBuilder cb) {
        return cb.like(cb.lower(expression), "%" + value.toLowerCase() + "%");
    }
}
