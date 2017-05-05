package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Interface that provides method to validate any instances of the {@link BasicEntity}.
 *
 * @param <T> any instance of {@link BasicEntity}
 * @author tmurzenkov
 */
public interface Validator<T extends BasicEntity> {

    /**
     * Sets the repository.
     *
     * @param cassandraRepository {@link CassandraRepository}
     * @return {@link Validator}
     */
    Validator<T> withRepository(CassandraRepository cassandraRepository);

    /**
     * Performs the validation logic. Might throw the instance of the {@link RuntimeException}.
     *
     * @param instance any instance of the {@link BasicEntity}
     */
    void doValidate(T instance);
}
