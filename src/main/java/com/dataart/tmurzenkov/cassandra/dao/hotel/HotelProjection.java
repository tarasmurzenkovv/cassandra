package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;

import java.util.UUID;

/**
 * Spring Data {@link Hotel} projection.
 *
 * @author tmurzenkov
 */
public interface HotelProjection {
    /**
     * Get the {@link Hotel} id.
     *
     * @return {@link UUID}
     */
    UUID getId();
}
