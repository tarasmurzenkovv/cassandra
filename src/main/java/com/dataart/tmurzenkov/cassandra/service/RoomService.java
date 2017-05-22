package com.dataart.tmurzenkov.cassandra.service;


import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
     * @param room {@link Room}
     */
    void addRoomToHotel(Room room);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param start start time period {@link Date}
     * @param end   end time period {@link Date}
     * @param hotelId {@link UUID}
     * @return {@link List} of {@link Room}
     */
    List<Room> findFreeRoomsInTheHotel(Date start, Date end, UUID hotelId);
}
