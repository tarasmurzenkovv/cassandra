package com.dataart.tmurzenkov.cassandra.service;


import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;

import java.util.List;

/**
 * Room service.
 *
 * @author tmurzenkov
 */
public interface RoomService {
    /**
     * Adds new room to the hotel.
     *
     * @param room {@link Room}
     * @return {@link Room}
     */
    Room addRoomToHotel(Room room);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param searchRequest start time period {@link SearchRequest}
     * @return {@link List} of {@link Room}
     */
    List<Room> findFreeRoomsInTheHotel(SearchRequest searchRequest);
}
