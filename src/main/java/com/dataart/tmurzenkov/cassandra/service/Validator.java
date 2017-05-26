package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Interface that provides method to validate any instances of the {@link BasicEntity}.
 *
 * @param <T> any instance of {@link BasicEntity}
 * @param <E> any instance of {@link Throwable}
 * @author tmurzenkov
 */
public interface Validator<T extends BasicEntity, E extends RuntimeException> {

    /**
     * Sets the condition to check. If this condition is true, then the exception will be thrown.
     *
     * @param conditionToCheck {@link Predicate}
     * @return {@link Validator}
     */
    Validator<T, E> withCondition(Predicate<T> conditionToCheck);

    /**
     * Performs the validation logic. Might throw the instance of the {@link RuntimeException}.
     *
     * @param instance any instance of the {@link BasicEntity}
     */
    void doValidate(T instance);

    /**
     * Throws the specific validation unchecked exception.
     *
     * @param exception any instance of {@link RuntimeException}
     * @return {@link Validator}
     */
    Validator<T, E> onConditionFailureThrow(Supplier<E> exception);
}
