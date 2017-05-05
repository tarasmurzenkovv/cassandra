package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import com.dataart.tmurzenkov.cassandra.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;

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
    private Validator<Hotel> validatorService;

    /**
     * Saves the hotel to the DB.
     *
     * @param hotel {@link Hotel}
     * @return {@link Hotel}
     */
    public Hotel addNewHotelToTheSystem(Hotel hotel) {
        LOGGER.info("Going to save the following entity into the DB: '%s'", hotel);
        validatorService.withRepository(hotelDao).doValidate(hotel);
        return hotelDao.save(hotel);
    }

    @Override
    public List<Hotel> findAllHotelsInTheCity(String city) {
        if (isEmpty(city)) {
            throw new IllegalArgumentException("Cannot find the hotels for the empty city name");
        }
        return hotelDao.findAllHotelsInTheCity(city);
    }
}
