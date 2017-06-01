package com.dataart.tmurzenkov.cassandra.service;


import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.AvailableRoomByHotelAndDate;

import java.util.List;

/**
 * AvailableRoomByHotelAndDate service.
 *
 * @author tmurzenkov
 */
public interface RoomService {
    /**
     * Adds new availableRoomByHotelAndDate to the hotel.
     *
     * @param availableRoomByHotelAndDate {@link AvailableRoomByHotelAndDate}
     * @return {@link AvailableRoomByHotelAndDate}
     */
    AvailableRoomByHotelAndDate addRoomToHotel(AvailableRoomByHotelAndDate availableRoomByHotelAndDate);

    /**
     * Finds free rooms for the given hotel in the given time period.
     *
     * @param searchRequest start time period {@link SearchRequest}
     * @return {@link List} of {@link AvailableRoomByHotelAndDate}
     */
    List<AvailableRoomByHotelAndDate> findFreeRoomsInTheHotel(SearchRequest searchRequest);
}
