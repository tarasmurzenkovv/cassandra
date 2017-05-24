package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.booking.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import com.dataart.tmurzenkov.cassandra.service.impl.validator.RecordExistsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * {@link GuestService} implementation.
 *
 * @author tmurzenkov
 */
@Service
public class GuestServiceImpl implements GuestService {
    @Autowired
    private GuestDao guestDao;
    @Autowired
    private RoomByGuestAndDateDao roomByGuestAndDateDao;
    @Autowired
    private RecordExistsValidator<Guest> guestRecordExistsValidator;

    @Override
    public void registerNewGuest(Guest guest) {
        if (null == guest) {
            throw new IllegalArgumentException("Cannot register an empty guest info. ");
        }
        final Guest validatedGuest = guestRecordExistsValidator.withRepository(guestDao).doValidate(guest);
        guestDao.save(validatedGuest);
    }

    @Override
    public List<Room> findBookedRoomsForTheGuestIdAndDate(UUID guestId, Date bookingDate) {
        final List<Room> bookedRooms = roomByGuestAndDateDao
                .getAllBookedRooms(guestId, bookingDate).stream().map(Room::new).collect(toList());
        return (bookedRooms.isEmpty()) ? emptyList() : bookedRooms;
    }

    @Override
    public void performBooking(BookingRequest bookingRequest) {
        roomByGuestAndDateDao.save(new RoomByGuestAndDate(bookingRequest));
    }
}
