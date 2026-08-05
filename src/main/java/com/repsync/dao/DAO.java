package com.repsync.dao;

import java.util.List;

/**
 * Generic DAO interface - defines standard CRUD operations.
 * 
 * Demonstrates: Interface (contract for all DAO classes)
 * Generics: Works with any model type T
 * 
 * @param <T> The model type (User, Exercise, etc.)
 */
public interface DAO<T> {

    /**
     * Insert a new record into the database.
     * @param item the object to insert
     * @return the generated ID, or -1 if failed
     */
    int insert(T item);

    /**
     * Update an existing record in the database.
     * @param item the object with updated values
     * @return true if update was successful
     */
    boolean update(T item);

    /**
     * Delete a record by its ID.
     * @param id the record ID to delete
     * @return true if deletion was successful
     */
    boolean delete(int id);

    /**
     * Find a single record by its ID.
     * @param id the record ID
     * @return the found object, or null if not found
     */
    T findById(int id);

    /**
     * Get all records from the table.
     * @return list of all records
     */
    List<T> findAll();
}
