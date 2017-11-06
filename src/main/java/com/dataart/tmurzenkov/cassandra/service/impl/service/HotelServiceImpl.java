package com.dataart.tmurzenkov.cassandra.service.impl.service;

import com.dataart.tmurzenkov.cassandra.dao.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.util.StringUtils.isEmpty;
import static com.dataart.tmurzenkov.cassandra.util.StringUtils.makeString;
import static java.lang.String.format;
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
    private ValidatorService<Hotel> validatorService;

    /**
     * Saves the hotel to the DB.
     *
     * @param hotel {@link Hotel}
     * @return {@link Hotel}
     */
    public Hotel addHotel(Hotel hotel) {
        doInsertInHotel(hotel);
        doInsertInHotelByCity(hotel);
        LOGGER.info("Successfully saved the new entity into the DB: '{}'", hotel);
        return hotel;
    }

    @Override
    public List<Hotel> findAllHotelsInTheCity(String city) {
        List<UUID> hotelIds = doFindHotelIdsByCityName(city);
        return  doFindHotelsByTheirIds(city, hotelIds);
    }

    private List<UUID> doFindHotelIdsByCityName(final String city) {
        if (isEmpty(city)) {
            throw new IllegalArgumentException("Cannot find the hotels for the empty city name");
        }
        return hotelByCityDao.findAllHotelIdsInTheCity(city).stream().map(HotelByCity::getId).collect(toList());
    }

    private List<Hotel> doFindHotelsByTheirIds(final String city, final List<UUID> hotelIds) {
        List<Hotel> hotelsForTheCity = hotelDao.findHotelInformationByIds(hotelIds);
        if (hotelsForTheCity.isEmpty()) {
            throw new RecordNotFoundException(format("Cannot find hotels for the given city '%s'", city));
        }
        LOGGER.info("Found the following hotels '{}' for the city '{}'", makeString(hotelsForTheCity), city);
        return hotelsForTheCity;
    }

    private void doInsertInHotelByCity(final Hotel registeredHotel) {
        final HotelByCity hotelByCity = new HotelByCity(registeredHotel);
        hotelByCityDao.insert(hotelByCity);
    }

    private void doInsertInHotel(final Hotel hotel) {
        validatorService.validateInfo(hotel);
        LOGGER.info("Going to save the following entity into the DB: '{}'", hotel);
        validatorService.checkIfExists(hotel);
        hotelDao.insert(hotel);
    }
}
