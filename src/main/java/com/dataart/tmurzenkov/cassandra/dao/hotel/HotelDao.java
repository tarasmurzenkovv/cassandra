package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
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
     * Find all {@link Hotel} by the provided list of {@link UUID} their ids.
     *
     * @param uuid hotel id
     * @return {@link List} of {@link Hotel}
     */
    @Query("select * from hotels where hotel_id in (?0)")
    List<Hotel> findAllHotelsByTheirIds(List<UUID> uuid);
}
