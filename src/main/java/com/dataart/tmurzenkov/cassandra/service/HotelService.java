package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;

import java.util.List;

/**
 * Hotel service.
 *
 * @author tmurzenkov
 */
public interface HotelService {

    /**
     * Adds new hotel to the system.
     *
     * @param hotel {@link Hotel}
     * @return added {@link Hotel}
     */
    Hotel addHotel(Hotel hotel);

    /**
     * Finds all hotels by the given city name.
     *
     * @param city {@link String} city name
     * @return {@link List} of the {@link Hotel}
     */
    List<Hotel> findAllHotelsInTheCity(String city);
}
