package com.dataart.tmurzenkov.cassandra.service.impl.validation;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class RoomValidatorServiceImpl implements ValidatorService<RoomByHotelAndDate> {
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;

    @Override
    public void validateInfo(final RoomByHotelAndDate entity) {
        if (null == entity) {
            throw new IllegalArgumentException("Cannot add the the room. It is empty. ");
        }
        if (entity.getId() == null) {
            final String nullHotelId =
                    format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                            entity.getRoomNumber());
            throw new IllegalArgumentException(nullHotelId);
        }
        if (entity.getRoomNumber() == null || 0 == entity.getRoomNumber()) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. ", entity.getRoomNumber()));
        }
    }

    @Override
    public void checkIfExists(final RoomByHotelAndDate entity) {
        if (null == hotelDao.findOne(entity.getId())) {
            final String cannotFindHotel = format("Cannot find the hotel for the given hotel id '%s'", entity.getId());
            throw new RecordNotFoundException(cannotFindHotel);
        }
        if (roomByHotelAndDateDao.exists(entity.getCompositeId())) {
            throw new RecordExistsException(format("The room is already inserted in DB. Room info '%s'", entity));
        }
    }
}
