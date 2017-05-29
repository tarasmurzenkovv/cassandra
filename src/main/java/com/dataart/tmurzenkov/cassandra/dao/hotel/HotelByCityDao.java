package com.dataart.tmurzenkov.cassandra.dao.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

/**
 * Fetches hotel ids by the city name.
 *
 * @author tmurzenkov
 */
public interface HotelByCityDao extends CassandraRepository<HotelByCity> {
    /**
     * Finds all {@link HotelByCity} for the given city name.
     *
     * @param cityName {@link String}
     * @return {@link List} of {@link HotelByCityDao}
     */
    @Query("select id from hotels_by_city where city = ?0")
    List<HotelByCity> findAllHotelIdsInTheCity(String cityName);
}
