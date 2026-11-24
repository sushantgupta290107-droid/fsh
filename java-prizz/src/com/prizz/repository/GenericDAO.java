package com.prizz.repository;

import java.util.List;

// Syllabus: Lectures 47-48 - Introductory Generics: Generic interface for DAO pattern
public interface GenericDAO<T, ID> {
    void create(T entity) throws Exception;
    T findById(ID id) throws Exception;
    List<T> findAll() throws Exception;
    void update(T entity) throws Exception;
    void delete(ID id) throws Exception;
}
