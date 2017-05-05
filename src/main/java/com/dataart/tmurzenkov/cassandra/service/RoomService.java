package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Room service to:
 * - Gets free rooms in a specific hotel for the current period.
 * - Adds a new room to a hotel.
 * -
 *
 * @author tmurzenkov
 */
public interface RoomService {
    /**
     * Adds new room to the hotel.
     *
     * @param room  {@link Room}
     * @param hotel {@link Hotel}
     */
    void addRoomToHotel(Room room, Hotel hotel);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param start start time period {@link LocalDateTime}
     * @param end   end time period {@link LocalDateTime}
     * @param hotel {@link Hotel}
     * @return {@link List} of {@link Room}
     */
    List<Room> findFreeRoomsInTheHotel(LocalDateTime start, LocalDateTime end, Hotel hotel);
}
