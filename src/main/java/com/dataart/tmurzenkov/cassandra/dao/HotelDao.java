package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

/**
 * Cassandra Spring data repository to persists the {@link Hotel} entity.
 * Will be implemented during the application context initialization.
 *
 * @author tmurzenkov
 */
public interface HotelDao extends CassandraRepository<Hotel> {
    /**
     * Finds all {@link Hotel} for the given city name.
     *
     * @param cityName {@link String}
     * @return {@link List} of {@link Hotel}
     */
    @Query("select * from hotel where hotel_city in(?0)")
    List<Hotel> findAllHotelsInTheCity(String cityName);
}
