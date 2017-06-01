package com.dataart.tmurzenkov.cassandra.dao.reservation;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Cassandra Spring data repository to persists the {@link RoomByHotelAndDateDao} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */

public interface RoomByHotelAndDateDao extends CassandraRepository<RoomByHotelAndDate> {

    /**
     * Queries all {@link RoomByHotelAndDate} for the provided hotel id, booked date and status.
     *
     * @param hotelId {@link UUID} hotel id
     * @param start   {@link LocalDate} start date
     * @param end     {@link LocalDate} end date
     * @return List of the {@link RoomByHotelAndDate}
     */
    @Query("select * from room_by_hotel_and_date_and_status where hotel_id = ?0 and booking_date >= ?1 and booking_date <= ?2")
    List<RoomByHotelAndDate> findBookedRoomInHotelForDate(UUID hotelId, LocalDate start, LocalDate end);
}
