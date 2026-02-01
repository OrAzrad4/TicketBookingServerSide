package com.hit.dao;
import java.util.List;
import java.io.IOException;
import java.io.Serializable;

public interface IDao<ID extends Serializable, T> {
    void delete(T entity) throws IllegalArgumentException, IOException;
    T find(ID id) throws IllegalArgumentException, IOException;
    boolean save(T entity) throws IllegalArgumentException, IOException;
    List<T> findAll() throws IOException;

}