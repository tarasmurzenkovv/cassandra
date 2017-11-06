package com.dataart.tmurzenkov.cassandra.service.impl.service;

import com.dataart.tmurzenkov.cassandra.dao.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * {@link BookingService} implementation.
 *
 * @author Taras_Murzenkov
 */
@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestServiceImpl.class);
    @Autowired
    private RoomByGuestAndDateDao roomByGuestAndDateDao;
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;

    @Override
    public BookingRequest performBooking(BookingRequest bookingRequest) {
        LOGGER.info("Going to perform booking with the following booking request '{}'", bookingRequest);
        validateBookingRequest(bookingRequest);
        doInsertInGuestAndDate(bookingRequest);
        doInsertInRoomByHotelAndDate(bookingRequest);
        return bookingRequest;
    }

    @Override
    public Integer generateConfirmationNumber(BookingRequest bookingRequest) {
        return bookingRequest.hashCode();
    }

    private void validateBookingRequest(BookingRequest bookingRequest) {
        if (null == bookingRequest) {
            throw new IllegalArgumentException("Cannot perform reservation for empty reservation request. ");
        }
    }

    private void doInsertInGuestAndDate(final BookingRequest bookingRequest) {
        final RoomByGuestAndDate guestAndDate = new RoomByGuestAndDate(bookingRequest);
        checkIfBooked(guestAndDate);
        guestAndDate.setConfirmationNumber(valueOf(generateConfirmationNumber(bookingRequest)));
        roomByGuestAndDateDao.insert(guestAndDate);
    }

    private void doInsertInRoomByHotelAndDate(final BookingRequest bookingRequest) {
        final RoomByHotelAndDate roomByHotelAndDate = new RoomByHotelAndDate(bookingRequest);
        checkIfExists(roomByHotelAndDate);
        roomByHotelAndDateDao.insert(roomByHotelAndDate);
    }

    private void checkIfBooked(RoomByGuestAndDate roomByGuestAndDate) {
        final String exceptionMessage = format("The following room is already booked. Room number: '%s', hotel id: '%s'",
                roomByGuestAndDate.getRoomNumber(), roomByGuestAndDate.getHotelId());
        if (roomByGuestAndDateDao.exists(roomByGuestAndDate.getCompositeId())) {
            throw new RecordExistsException(exceptionMessage);
        }
    }


    private void checkIfExists(RoomByHotelAndDate roomByHotelAndDate) {
        final String exceptionMessage = format("The following room does not exists. Room number: '%s', hotel id: '%s',",
                roomByHotelAndDate.getRoomNumber(), roomByHotelAndDate.getId());
        if (!roomByHotelAndDateDao.exists(roomByHotelAndDate.getCompositeId())) {
            throw new RecordNotFoundException(exceptionMessage);
        }
    }
}
