package com.dataart.tmurzenkov.cassandra.service.impl.validator;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.Validator;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.stereotype.Service;

/**
 * {@link Validator} implementation to check if the given entity exists in the data base.
 * Throws the {@link RecordExistsException} if the validation didn't pass.
 *
 * @param <T> any instance of {@link BasicEntity}
 * @author tmurzenkov
 * @see RecordExistsException
 */

@Service
public class RecordExistsValidator<T extends BasicEntity> implements Validator<T> {
    private CassandraRepository<T> cassandraRepository;

    @Override
    public RecordExistsValidator<T> withRepository(CassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
        return this;
    }

    @Override
    public void doValidate(T entity) throws IllegalArgumentException {
        MapId compositeId = entity.getCompositeId();
        if (cassandraRepository.exists(compositeId)) {
            throw new RecordExistsException("Duplicate entry found in DB. The id of the entity: " + entity.getId().toString());
        }
    }
}
