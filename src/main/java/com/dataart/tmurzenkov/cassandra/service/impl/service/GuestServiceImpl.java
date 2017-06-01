package com.dataart.tmurzenkov.cassandra.service.impl.service;

import com.dataart.tmurzenkov.cassandra.dao.hotel.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.reservation.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.service.util.DateUtils.format;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.makeString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

/**
 * {@link GuestService} implementation.
 *
 * @author tmurzenkov
 */
@Service
public class GuestServiceImpl implements GuestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestServiceImpl.class);
    @Autowired
    private GuestDao guestDao;
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Autowired
    private RoomByGuestAndDateDao roomByGuestAndDateDao;
    @Autowired
    private ValidatorService<Guest> guestValidatorService;

    @Override
    public Guest registerNewGuest(Guest guest) {
        guestValidatorService.validateInfo(guest);
        guestValidatorService.checkIfExists(guest);
        final Guest savedGuestInfo = guestDao.insert(guest);
        LOGGER.info("Successfully registered the new guest '{}'", savedGuestInfo);
        return savedGuestInfo;
    }

    @Override
    public List<RoomByHotelAndDate> findBookedRoomsForTheGuestIdAndDate(UUID guestId, LocalDate bookingDate) {
        validateSearchParameters(guestId, bookingDate);
        LOGGER.debug("Going to look for the booked rooms for the guest id '{}' and '{}'", guestId, bookingDate);
        final List<RoomByHotelAndDate> bookedRoomByHotelAndDates = roomByGuestAndDateDao
                .getAllBookedRooms(guestId, bookingDate)
                .stream()
                .map(RoomByHotelAndDate::new)
                .collect(toList());
        validateFoundRoomsByHotelAndDate(guestId, bookingDate, bookedRoomByHotelAndDates);
        LOGGER.debug("Guest with id '{}' has the following booked rooms '{}'", guestId, makeString(bookedRoomByHotelAndDates));
        return bookedRoomByHotelAndDates;
    }

    @Override
    public BookingRequest performBooking(BookingRequest bookingRequest) {
        validateBookingRequest(bookingRequest);
        doInsertInGuestAndDate(bookingRequest);
        doInsertInRoomByHotelAndDate(bookingRequest);
        return bookingRequest;
    }

    private void validateFoundRoomsByHotelAndDate(final UUID guestId, final LocalDate bookingDate, final List<RoomByHotelAndDate> bookedRoomByHotelAndDates) {
        if (bookedRoomByHotelAndDates.isEmpty()) {
            final String message = format("Cannot find the booked rooms for the customer id '%s' and given date '%s'",
                    guestId, format(bookingDate));
            throw new RecordNotFoundException(message);
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

    private void validateBookingRequest(BookingRequest bookingRequest) {
        if (null == bookingRequest) {
            throw new IllegalArgumentException("Cannot perform reservation for empty reservation request. ");
        }
    }

    private void validateSearchParameters(UUID guestId, LocalDate bookingDate) {
        if (null == guestId) {
            throw new IllegalArgumentException("Cannot perform search of the booked room for the null guest id ");
        }
        if (null == bookingDate) {
            throw new IllegalArgumentException("Cannot perform search of the booked room for the null reservation date ");
        }
    }

    private void checkIfExists(RoomByHotelAndDate roomByHotelAndDate) {
        final String exceptionMessage = format("The following room does not exists. Room number: '%s', hotel id: '%s',",
                roomByHotelAndDate.getRoomNumber(), roomByHotelAndDate.getId());
        RoomByHotelAndDate one = roomByHotelAndDateDao.findOne(roomByHotelAndDate.getId(),
                roomByHotelAndDate.getDate(),
                roomByHotelAndDate.getRoomNumber());
        if (one == null) {
            throw new RecordNotFoundException(exceptionMessage);
        }
    }

    private void checkIfBooked(RoomByGuestAndDate roomByGuestAndDate) {
        final String exceptionMessage = format("The following room is already booked. Room number: '%s', hotel id: '%s'",
                roomByGuestAndDate.getRoomNumber(), roomByGuestAndDate.getHotelId());
        if (roomByGuestAndDateDao.exists(roomByGuestAndDate.getCompositeId())) {
            throw new RecordExistsException(exceptionMessage);
        }
    }

    private Integer generateConfirmationNumber(BookingRequest bookingRequest) {
        return bookingRequest.hashCode();
    }
}
