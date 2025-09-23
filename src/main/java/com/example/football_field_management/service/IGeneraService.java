package com.example.football_field_management.service;

import java.util.Optional;

public interface IGeneraService<T> {
    Iterable<T> findAll();
    Optional<T> findById(Long id);
    void save(T t);
    void remote(Long id);
}