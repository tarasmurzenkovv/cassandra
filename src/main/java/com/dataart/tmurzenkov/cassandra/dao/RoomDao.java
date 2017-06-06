package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Set;
import java.util.UUID;

/**
 * Spring data repository to work with {@link Room}.
 *
 * @author tmurzenkov
 */
public interface RoomDao extends CassandraRepository<Room> {
    /**
     * Finds all rooms for the given hotel id.
     *
     * @param hotelId {@link UUID}
     * @return set of found rooms
     */
    @Query("select * from room_by_hotel where hotel_id = ?0")
    Set<Room> findAllRoomsByHotelId(UUID hotelId);
}
