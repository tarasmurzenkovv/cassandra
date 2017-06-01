package com.dataart.tmurzenkov.cassandra.dao.reservation;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Cassandra Spring data repository to persists the {@link RoomByHotelAndDateDao} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */

public interface RoomByHotelAndDateDao extends CassandraRepository<RoomByHotelAndDate> {

}
