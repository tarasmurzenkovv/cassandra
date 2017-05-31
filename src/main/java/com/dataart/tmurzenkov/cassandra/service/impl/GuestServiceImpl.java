package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus.BOOKED;
import static com.dataart.tmurzenkov.cassandra.service.impl.RecordValidator.validator;
import static com.dataart.tmurzenkov.cassandra.service.util.DateUtils.format;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;
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
    private final GuestDao guestDao;
    private final RoomDao roomDao;
    private final RoomByGuestAndDateDao roomByGuestAndDateDao;
    private final RoomByHotelAndDateDao roomByHotelAndDateDao;

    /**
     * The below services will be autowired by Spring automatically.
     *
     * @param guestDao              {@link GuestDao}
     * @param roomByGuestAndDateDao {@link RoomByGuestAndDateDao}
     * @param roomDao               {@link RoomDao}
     * @param roomByHotelAndDateDao {@link RoomByHotelAndDateDao}
     */
    public GuestServiceImpl(GuestDao guestDao,
                            RoomDao roomDao,
                            RoomByGuestAndDateDao roomByGuestAndDateDao,
                            RoomByHotelAndDateDao roomByHotelAndDateDao) {
        this.guestDao = guestDao;
        this.roomByGuestAndDateDao = roomByGuestAndDateDao;
        this.roomByHotelAndDateDao = roomByHotelAndDateDao;
        this.roomDao = roomDao;
    }

    @Override
    public Guest registerNewGuest(Guest guest) {
        validateGuest(guest);
        checkIfRegistered(guest);
        final Guest savedGuestInfo = guestDao.insert(guest);
        LOGGER.info("Successfully registered the new guest '{}'", savedGuestInfo);
        return savedGuestInfo;
    }

    @Override
    public List<Room> findBookedRoomsForTheGuestIdAndDate(UUID guestId, LocalDate bookingDate) {
        validateSearchParameters(guestId, bookingDate);
        LOGGER.debug("Going to look for the booked rooms for the guest id '{}' and '{}'", guestId, bookingDate);
        final List<Room> bookedRooms = roomByGuestAndDateDao
                .getAllBookedRooms(guestId, bookingDate).stream().map(Room::new).collect(toList());
        if (bookedRooms.isEmpty()) {
            final String message = format("Cannot find the booked rooms for the customer id '%s' and given date '%s'",
                    guestId, format(bookingDate));
            throw new RecordNotFoundException(message);
        }
        LOGGER.debug("Guest with id '{}' has the following booked rooms '{}'", guestId, makeString(bookedRooms));
        return bookedRooms;
    }

    @Override
    public BookingRequest performBooking(BookingRequest bookingRequest) {
        validateBookingRequest(bookingRequest);
        final RoomByHotelAndDate hotelAndDate = new RoomByHotelAndDate(bookingRequest, BOOKED);
        final RoomByGuestAndDate guestAndDate = new RoomByGuestAndDate(bookingRequest);
        checkIfExists(hotelAndDate);
        checkIfBooked(hotelAndDate);
        guestAndDate.setConfirmationNumber(valueOf(generateConfirmationNumber(bookingRequest)));
        roomByHotelAndDateDao.insert(hotelAndDate);
        roomByGuestAndDateDao.insert(guestAndDate);
        return bookingRequest;
    }

    private void validateGuest(Guest guest) {
        if (null == guest) {
            throw new IllegalArgumentException("Cannot register the empty guest info. ");
        }
        if (null == guest.getId()) {
            throw new IllegalArgumentException("Cannot register guest info with empty id. ");
        }
        if (isEmpty(guest.getFirstName())) {
            throw new IllegalArgumentException("Cannot register guest info with empty first name. ");
        }
        if (isEmpty(guest.getLastName())) {
            throw new IllegalArgumentException("Cannot register guest info with empty last name. ");
        }
    }

    private void validateBookingRequest(BookingRequest bookingRequest) {
        if (null == bookingRequest) {
            throw new IllegalArgumentException("Cannot perform booking for empty booking request. ");
        }
    }

    private void validateSearchParameters(UUID guestId, LocalDate bookingDate) {
        if (null == guestId) {
            throw new IllegalArgumentException("Cannot perform search of the booked room for the null guest id ");
        }
        if (null == bookingDate) {
            throw new IllegalArgumentException("Cannot perform search of the booked room for the null booking date ");
        }
    }

    private void checkIfExists(RoomByHotelAndDate roomByHotelAndDate) {
        final String exceptionMessage = format("The following room does not exists. Room number: '%s', hotel id: '%s',",
                roomByHotelAndDate.getRoomNumber(), roomByHotelAndDate.getId());
        final Room room = new Room(roomByHotelAndDate);
        validator()
                .withCondition(e -> !roomDao.exists(e.getCompositeId()))
                .onConditionFailureThrow(() -> new RecordNotFoundException(exceptionMessage))
                .doValidate(room);
    }

    private void checkIfBooked(RoomByHotelAndDate roomByHotelAndDate) {
        final String exceptionMessage = format("The following room is already booked. Room number: '%s', hotel id: '%s'",
                roomByHotelAndDate.getRoomNumber(), roomByHotelAndDate.getId());
        final RoomByHotelAndDate exists = roomByHotelAndDateDao.findOne(roomByHotelAndDate.getCompositeId());
        if (exists != null && BOOKED == exists.getStatus()) {
            throw new RecordExistsException(exceptionMessage);
        }
    }

    private void checkIfRegistered(Guest guest) {
        final String exceptionMessage = format("The guest information is already stored in DB. "
                + "Guest id: '%s', name: '%s', surname: '%s'", guest.getId(), guest.getFirstName(), guest.getLastName());
        validator()
                .withCondition(e -> guestDao.exists(e.getCompositeId()))
                .onConditionFailureThrow(() -> new RecordExistsException(exceptionMessage))
                .doValidate(guest);
    }

    private Integer generateConfirmationNumber(BookingRequest bookingRequest) {
        return bookingRequest.hashCode();
    }
}
