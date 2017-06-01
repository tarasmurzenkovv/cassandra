package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;

/**
 * Vallidator service interface for the {@link BasicEntity}.
 *
 * @param <T> {@link BasicEntity}
 * @author tmurzenkov
 */
public interface ValidatorService<T extends BasicEntity> {
    /**
     * Should validate the internals of the passed entity.
     *
     * @param entity {@link BasicEntity}
     */
    void validateInfo(T entity);

    /**
     * Should check if such record exists in db.
     *
     * @param entity {@link BasicEntity}
     */
    void checkIfExists(T entity);
}
