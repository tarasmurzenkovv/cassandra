package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


/**
 * Cassandra Spring data repository to persists the {@link RoomByHotelAndDate} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface RoomByHotelAndDateDao extends CassandraRepository<RoomByHotelAndDate> {
    /**
     * Finds all rooms for the give hotel id and date range.
     *
     * @param hotelId {@link UUID}
     * @param start   {@link LocalDate}
     * @param end     {@link LocalDate}
     * @return list of {@link RoomByHotelAndDate}
     */
    @Query("select * from room_by_hotel_and_date where hotel_id = ?0 and date >= ?1 and date <= ?2")
    Set<RoomByHotelAndDate> findAllRoomsForHotelIdAndPeriod(UUID hotelId, LocalDate start, LocalDate end);
}
