package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.LocalDate;
import java.util.List;
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
    @Query("select * from available_rooms_by_hotel_date where hotel_id = ?0 and date >= ?1 and date <= ?2")
    List<RoomByHotelAndDate> findAvailableRoomsForHotelId(UUID hotelId, LocalDate start, LocalDate end);

    /**
     * Finds one {@link RoomByHotelAndDate}.
     *
     * @param id         {@link UUID}
     * @param date       {@link LocalDate}
     * @param roomNumber {@link Integer}
     * @return list of {@link RoomByHotelAndDate}
     */
    @Query("select * from available_rooms_by_hotel_date where hotel_id = ?0 and date = ?1 and room_number = ?2")
    RoomByHotelAndDate findOne(UUID id, LocalDate date, Integer roomNumber);
}
