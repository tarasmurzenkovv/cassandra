package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Cassandra Spring data repository to persists the {@link Hotel} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface HotelDao extends CassandraRepository<Hotel> {

    /**
     * Finds one hotel by its id.
     *
     * @param hotelId {@link UUID}
     * @return {@link Hotel}
     */
    @Query("select * from hotels where hotel_id = ?0")
    Hotel findOne(UUID hotelId);

    /**
     * Finds all hotel information by the provided list of of their ids.
     *
     * @param hotelIds list of hotel id {@link UUID}
     * @return list of {@link Hotel}
     */
    @Query("select * from hotels where hotel_id in (?0)")
    List<Hotel> findHotelInformationByIds(List<UUID> hotelIds);
}
