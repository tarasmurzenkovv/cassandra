package com.dataart.tmurzenkov.cassandra.dao.booking;

import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.UUID;

/**
 * Cassandra Spring data repository to persists the {@link Guest} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface GuestDao extends CassandraRepository<Guest> {
    /**
     * Finds {@link Guest} by id.
     *
     * @param guestId {@link UUID}
     * @return {@link Guest}
     */
    @Query("select * from guests where guest_id = ?0")
    Guest findOne(UUID guestId);
}
