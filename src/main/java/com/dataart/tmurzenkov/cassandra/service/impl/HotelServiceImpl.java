package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import com.dataart.tmurzenkov.cassandra.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;
import static java.util.stream.Collectors.toList;

/**
 * Service to manage {@link Hotel}.
 *
 * @author tmurzenkov
 */
@Service
public class HotelServiceImpl implements HotelService {
    private static Logger LOGGER = LoggerFactory.getLogger(HotelServiceImpl.class);
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private HotelByCityDao hotelByCityDao;
    @Autowired
    private Validator<Hotel> validatorService;

    /**
     * Saves the hotel to the DB.
     *
     * @param hotel {@link Hotel}
     * @return {@link Hotel}
     */
    public Hotel addHotel(Hotel hotel) {
        LOGGER.info("Going to save the following entity into the DB: '{}'", hotel);
        validatorService.withRepository(hotelDao).doValidate(hotel);
        final Hotel registeredHotel = hotelDao.save(hotel);
        hotelByCityDao.save(new HotelByCity(registeredHotel));
        return registeredHotel;
    }

    @Override
    public List<Hotel> findAllHotelsInTheCity(String city) {
        if (isEmpty(city)) {
            throw new IllegalArgumentException("Cannot find the hotels for the empty city name");
        }
        List<HotelByCity> allHotelIdsInTheCity = hotelByCityDao.findAllHotelIdsInTheCity(city);
        List<UUID> uuids = allHotelIdsInTheCity.stream().map(HotelByCity::getHotelId).collect(toList());
        return hotelDao.findAllHotelsByTheirIds(uuids);
    }
}
