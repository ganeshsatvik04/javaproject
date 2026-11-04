package com.careermatch.dao;

import java.util.List;

public interface DatabaseOperations<T> {
    boolean insert(T item);
    List<T> getAll();
    boolean update(T item);
    boolean delete(int id);
}