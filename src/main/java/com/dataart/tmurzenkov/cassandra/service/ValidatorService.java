package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;

public interface ValidatorService<T extends BasicEntity> {
    void validateInfo(T entity);

    void checkIfExists(T entity);
}
