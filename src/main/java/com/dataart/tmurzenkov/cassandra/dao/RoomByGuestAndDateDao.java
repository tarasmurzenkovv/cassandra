package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring data repository to work with the {@link RoomByGuestAndDate}.
 *
 * @author tmurzenkov
 */
public interface RoomByGuestAndDateDao extends CassandraRepository<RoomByGuestAndDate> {

    /**
     * Selects/finds all instances of the {@link RoomByGuestAndDate} for the specified guest id and reservation date.
     *
     * @param guestId     {@link UUID} guest id
     * @param bookingDate {@link LocalDate} date
     * @return list of the instances {@link RoomByGuestAndDate}
     */
    @Query("select * from room_booked_by_guest_and_date where guest_id = ?0 and booking_date = ?1")
    List<RoomByGuestAndDate> getAllBookedRooms(UUID guestId, LocalDate bookingDate);
}
