package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Cassandra Spring data repository to persists the {@link Room} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface RoomDao extends CassandraRepository<Room> {

    /**
     * Finds available rooms in the hotel by its id.
     *
     * @param hotelId {@link UUID} hotel id
     * @return {@link List} of {@link Room}
     */
    @Query("select * from available_rooms_by_hotel_date where hotel_id = (?0)")
    List<Room> findAvailableRooms(UUID hotelId);
}
