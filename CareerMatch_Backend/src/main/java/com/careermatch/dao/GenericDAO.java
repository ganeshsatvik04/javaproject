package com.careermatch.dao;

import java.util.List;
import java.util.Optional;

/**
 * Reusable generic abstract DAO repository class demonstrating Java Generics.
 * Defines standard CRUD operations and parameterized helpers.
 *
 * @param <T> The domain entity type
 */
public abstract class GenericDAO<T> implements DatabaseOperations<T> {

    @Override
    public abstract boolean insert(T item);

    @Override
    public abstract List<T> getAll();

    @Override
    public abstract boolean update(T item);

    @Override
    public abstract boolean delete(int id);

    /**
     * Finds an entity by primary key ID using Java Optional.
     */
    public abstract Optional<T> findById(int id);

    /**
     * Counts total entities in repository.
     */
    public int count() {
        List<T> list = getAll();
        return list != null ? list.size() : 0;
    }
}
