package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.Validator;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link Validator} implementation.
 *
 * @param <T> any instance of {@link BasicEntity}
 * @param <E> any instance of {@link RuntimeException}
 * @author tmurzenkov
 * @see RecordExistsException
 */

public class RecordValidator<T extends BasicEntity, E extends RuntimeException> implements Validator<T, E> {
    private E exceptionToThrow;
    private Predicate<T> conditionOfFailure;

    /**
     * Convenient factory method.
     *
     * @param <T> any instance of {@link BasicEntity}
     * @param <E> any instance of {@link RuntimeException}
     * @return {@link RecordValidator}
     */
    public static <T extends BasicEntity, E extends RuntimeException> RecordValidator<T, E> validator() {
        return new RecordValidator<>();
    }

    @Override
    public RecordValidator<T, E> withCondition(Predicate<T> conditionOfFailure) {
        this.conditionOfFailure = conditionOfFailure;
        return this;
    }

    @Override
    public void doValidate(T entity) {
        if (!conditionOfFailure.test(entity)) {
            throw exceptionToThrow;
        }
    }

    @Override
    public RecordValidator<T, E> onConditionFailureThrow(Supplier<E> exceptionSupplier) {
        this.exceptionToThrow = exceptionSupplier.get();
        return this;
    }

    /**
     * Checks if such record exists in data base.
     *
     * @param repository {@link CassandraRepository} to check with
     * @param entity     {@link BasicEntity} entity yo check with
     * @param message    {@link String} message to display on exception
     * @param <T>        type parameters, should extend {@link BasicEntity}
     */
    public static <T extends BasicEntity> void validatePresenceInDb(CassandraRepository<T> repository, T entity, String message) {
        validator()
                .withCondition(e -> !repository.exists(e.getCompositeId()))
                .onConditionFailureThrow(() -> new RecordExistsException(message))
                .doValidate(entity);
    }
}
