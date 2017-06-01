package com.dataart.tmurzenkov.cassandra.service;


import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;

import java.util.List;

/**
 * RoomByHotelAndDate service.
 *
 * @author tmurzenkov
 */
public interface RoomService {
    /**
     * Adds new roomByHotelAndDate to the hotel.
     *
     * @param roomByHotelAndDate {@link RoomByHotelAndDate}
     * @return {@link RoomByHotelAndDate}
     */
    RoomByHotelAndDate addRoomToHotel(RoomByHotelAndDate roomByHotelAndDate);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param searchRequest start time period {@link SearchRequest}
     * @return {@link List} of {@link RoomByHotelAndDate}
     */
    List<RoomByHotelAndDate> findFreeRoomsInTheHotel(SearchRequest searchRequest);
}
