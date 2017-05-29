package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Interface that provides method to validate any instances of the {@link BasicEntity}.
 * The builder pattern is used to implement this type.
 *
 * @param <T> any instance
 * @param <E> any instance of {@link Throwable}
 * @author tmurzenkov
 */
public interface Validator<T, E extends RuntimeException> {

    /**
     * Sets the condition to check. If this condition is true, then the exception will be thrown.
     *
     * @param conditionToCheck {@link Predicate}
     * @return {@link Validator}
     */
    Validator<T, E> withCondition(Predicate<T> conditionToCheck);

    /**
     * Throws the specific validation unchecked exceptionSupplier.
     *
     * @param exceptionSupplier any instance of {@link Supplier} that holds instance of {@link RuntimeException}
     * @return {@link Validator}
     */
    Validator<T, E> onConditionFailureThrow(Supplier<E> exceptionSupplier);

    /**
     * Executes the validation logic. The example of such execution could be the following:
     * <code>
     * if(Validator.conditionToCheck.test(entityToValidate){
     * throw exceptionSupplier.get();
     * }
     * <p>
     * </code>
     *
     * @param instance any instance
     */
    void doValidate(T instance);
}
