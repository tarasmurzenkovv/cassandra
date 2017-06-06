package com.dataart.tmurzenkov.cassandra.service.impl.validation;

import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Implementation of the {@link ValidatorService} for the {@link RoomByHotelAndDate}.
 *
 * @author tmurzenkov
 */
@Service
public class RoomValidatorServiceImpl implements ValidatorService<Room> {
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private RoomDao roomDao;

    @Override
    public void validateInfo(final Room roomByHotelAndDate) {
        if (null == roomByHotelAndDate) {
            throw new IllegalArgumentException("Cannot add the the room. It is empty. ");
        }
        if (roomByHotelAndDate.getHotelId() == null) {
            final String nullHotelId =
                    format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                            roomByHotelAndDate.getRoomNumber());
            throw new IllegalArgumentException(nullHotelId);
        }
        if (null == roomByHotelAndDate.getRoomNumber() || 0 == roomByHotelAndDate.getRoomNumber()) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. ", roomByHotelAndDate.getRoomNumber()));
        }
    }

    @Override
    public void checkIfExists(final Room room) {
        if (null == hotelDao.findOne(room.getHotelId())) {
            final String cannotFindHotel = format("Cannot find the hotel for the given hotel id '%s'", room.getHotelId());
            throw new RecordNotFoundException(cannotFindHotel);
        }
        if (roomDao.exists(room.getCompositeId())) {
            throw new RecordExistsException(format("The room is already inserted in DB. Room info '%s'", room));
        }
    }
}
