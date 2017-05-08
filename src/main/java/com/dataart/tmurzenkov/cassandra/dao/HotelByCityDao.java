package com.dataart.tmurzenkov.cassandra.dao;

import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.HotelByCity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Fetches hotel ids by the city name.
 *
 * @author tmurzenkov
 */
public interface HotelByCityDao extends CassandraRepository<HotelByCity>{
    /**
     * Finds all {@link UUID} of the {@link Hotel} for the given city name.
     *
     * @param cityName {@link String}
     * @return {@link List} of {@link UUID}
     */
    @Query("select * from hotels_by_city where city in (?0);")
    List<HotelByCity> findAllHotelIdsInTheCity(String cityName);
}
