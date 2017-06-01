package com.dataart.tmurzenkov.cassandra.service.impl.validation;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;
import static java.lang.String.format;

@Service
public class HotelValidatorServiceImpl implements ValidatorService<Hotel> {
    @Autowired
    private HotelDao hotelDao;

    public void validateInfo(Hotel hotel) {
        if (null == hotel) {
            throw new IllegalArgumentException("Cannot add the empty hotel info. ");
        }
        if (null == hotel.getId()) {
            throw new IllegalArgumentException("Cannot add the hotel with empty id. ");
        }
        if (isEmpty(hotel.getName())) {
            throw new IllegalArgumentException("Cannot add the hotel with empty name. ");
        }
        if (isEmpty(hotel.getPhone())) {
            throw new IllegalArgumentException("Cannot add the hotel with empty phone field. ");
        }
        validateHotelAddress(hotel.getAddress());
    }

    public void checkIfExists(Hotel hotel) {
        final String message = format("Such hotel information is already added to the data base '%s'", hotel);
        if (hotelDao.exists(hotel.getCompositeId())) {
            throw new RecordExistsException(message);
        }
    }

    private void validateHotelAddress(Address address) {
        if (null == address) {
            throw new IllegalArgumentException("Cannot add the hotel with empty address info. ");
        }
        if (isEmpty(address.getCity())) {
            throw new IllegalArgumentException("Cannot add the hotel with empty city. ");
        }
        if (isEmpty(address.getCountry())) {
            throw new IllegalArgumentException("Cannot add the hotel with empty country. ");
        }
        if (isEmpty(address.getPostalCode())) {
            throw new IllegalArgumentException("Cannot add the hotel with empty postal code. ");
        }
    }
}
