package com.dataart.tmurzenkov.cassandra.service;


import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;

import java.util.Set;

/**
 * RoomByHotelAndDate service.
 *
 * @author tmurzenkov
 */
public interface RoomService {
    /**
     * Adds new roomByHotelAndDate to the hotel.
     *
     * @param roomByHotelAndDate {@link Room}
     * @return {@link RoomByHotelAndDate}
     */
    Room addRoomToHotel(Room roomByHotelAndDate);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param searchRequest start time period {@link SearchRequest}
     * @return {@link Set} of {@link RoomByHotelAndDate}
     */
    Set<Room> findFreeRoomsInTheHotel(SearchRequest searchRequest);
}
