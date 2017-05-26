package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Cassandra Spring data repository to persists the {@link Guest} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface GuestDao extends CassandraRepository<Guest> {
}
