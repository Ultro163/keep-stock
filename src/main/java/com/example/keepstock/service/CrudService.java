package com.example.keepstock.service;

public interface CrudService<T, K> {
    T save(T entity);

    T update(T entity);

    void delete(K id);

    T getById(K id);
}