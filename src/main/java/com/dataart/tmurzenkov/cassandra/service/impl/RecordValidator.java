package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.Validator;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@link Validator} implementation to check if the given entity exists in the data base.
 * Throws the {@link RecordExistsException} if the validation didn't pass.
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
    public RecordValidator<T, E> onConditionFailureThrow(Supplier<E> exception) {
        this.exceptionToThrow = exception.get();
        return this;
    }
}
