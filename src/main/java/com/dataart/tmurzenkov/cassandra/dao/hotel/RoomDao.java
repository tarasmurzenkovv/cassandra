package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;


/**
 * Cassandra Spring data repository to persists the {@link Room} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface RoomDao extends CassandraRepository<Room> {

}
