package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.room.AvailableRoomByHotelAndDate;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


/**
 * Cassandra Spring data repository to persists the {@link AvailableRoomByHotelAndDate} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface AvailableRoomByHotelAndDateDao extends CassandraRepository<AvailableRoomByHotelAndDate> {
    /**
     * Finds all rooms for the give hotel id and date range.
     *
     * @param hotelId {@link UUID}
     * @param start   {@link LocalDate}
     * @param end     {@link LocalDate}
     * @return list of {@link AvailableRoomByHotelAndDate}
     */
    @Query("select * from available_rooms_by_hotel_date where hotel_id = ?0 and date >= ?1 and date <= ?2")
    List<AvailableRoomByHotelAndDate> findAvailableRoomsForHotelId(UUID hotelId, LocalDate start, LocalDate end);

    /**
     * Finds one {@link AvailableRoomByHotelAndDate}.
     *
     * @param id         {@link UUID}
     * @param date       {@link LocalDate}
     * @param roomNumber {@link Integer}
     * @return list of {@link AvailableRoomByHotelAndDate}
     */
    @Query("select * from available_rooms_by_hotel_date where hotel_id = ?0 and date = ?1 and room_number = ?2")
    AvailableRoomByHotelAndDate findOne(UUID id, LocalDate date, Integer roomNumber);
}
