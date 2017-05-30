package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dataart.tmurzenkov.cassandra.service.impl.RecordValidator.validatePresenceInDb;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.makeString;
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
    private final HotelDao hotelDao;
    private final HotelByCityDao hotelByCityDao;

    /**
     * The below services will be autowired by Spring automatically.
     *
     * @param hotelDao       {@link GuestDao}
     * @param hotelByCityDao {@link HotelByCityDao}
     */
    public HotelServiceImpl(HotelDao hotelDao, HotelByCityDao hotelByCityDao) {
        this.hotelDao = hotelDao;
        this.hotelByCityDao = hotelByCityDao;
    }

    /**
     * Saves the hotel to the DB.
     *
     * @param hotel {@link Hotel}
     * @return {@link Hotel}
     */
    public Hotel addHotel(Hotel hotel) {
        if (null == hotel) {
            throw new IllegalArgumentException("Cannot add the empty hotel info. ");
        }
        LOGGER.info("Going to save the following entity into the DB: '{}'", hotel);
        checkIfExist(hotel);
        final Hotel registeredHotel = hotelDao.insert(hotel);
        hotelByCityDao.insert(new HotelByCity(registeredHotel));
        LOGGER.info("Successfully saved the new entity into the DB: '{}'", registeredHotel);
        return registeredHotel;
    }

    @Override
    public List<Hotel> findAllHotelsInTheCity(String city) {
        if (isEmpty(city)) {
            throw new IllegalArgumentException("Cannot find the hotels for the empty city name");
        }
        List<Hotel> hotelsForTheCity = hotelByCityDao.findAllHotelIdsInTheCity(city).stream()
                .map(hotelByCity -> hotelDao.findOne(hotelByCity.getId())).collect(toList());
        if (hotelsForTheCity.isEmpty()) {
            throw new RecordNotFoundException(format("Cannot find hotels for the given city '%s'", city));
        }
        LOGGER.info("Found the following hotels '{}' for the city '{}'", makeString(hotelsForTheCity), city);
        return hotelsForTheCity;
    }

    private void checkIfExist(Hotel hotel) {
        final String message = format("Such hotel information is already added to the data base '%s'", hotel);
        validatePresenceInDb(hotelDao, hotel, message);
    }
}
