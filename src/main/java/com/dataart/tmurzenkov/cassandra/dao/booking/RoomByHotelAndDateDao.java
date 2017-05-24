package com.dataart.tmurzenkov.cassandra.dao.booking;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.datastax.driver.core.LocalDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Set;
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
     * @param hotelId    {@link UUID} hotel id
     * @param bookedDate {@link LocalDate} booked date
     * @param status     {@link String} status -- reserved/booked/free
     * @return set of the {@link RoomByHotelAndDate}
     */
    @Query("select * from room_booking where hotel_id = ?0 and booked_date = ?1 and status = ?2")
    Set<RoomByHotelAndDate> findBookedRoomInHotelForDate(UUID hotelId, LocalDate bookedDate, String status);
}
