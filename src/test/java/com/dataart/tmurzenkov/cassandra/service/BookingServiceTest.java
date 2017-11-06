package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.dao.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByGuestAndDate;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.impl.service.BookingServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.service.GuestServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.validation.GuestValidatorServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private RoomDao roomDao;
    @Mock
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Mock
    private RoomByGuestAndDateDao byGuestAndDateDao;
    @InjectMocks
    private BookingServiceImpl sut;

    @Test
    public void shouldBookRoom() {
        final Integer roomNumber = 1;
        final BookingRequest bookingRequest = getBookingRequest(roomNumber);
        final RoomByHotelAndDate roomByHotelAndDate = new RoomByHotelAndDate(bookingRequest);
        final RoomByGuestAndDate expectedRoomByGuestAndDate = new RoomByGuestAndDate(bookingRequest);
        final Room room = new Room(roomByHotelAndDate);
        expectedRoomByGuestAndDate.setConfirmationNumber(valueOf(bookingRequest.hashCode()));

        when(byGuestAndDateDao.insert(any(RoomByGuestAndDate.class))).thenReturn(expectedRoomByGuestAndDate);
        when(roomDao.exists(eq(room.getCompositeId()))).thenReturn(true);

        sut.performBooking(bookingRequest);

        verify(roomDao).exists(eq(room.getCompositeId()));
        verify(roomByHotelAndDateDao).insert(eq(roomByHotelAndDate));
        verify(byGuestAndDateDao).insert(eq(expectedRoomByGuestAndDate));
        verify(byGuestAndDateDao, never()).save(any(RoomByGuestAndDate.class));
    }

    @Test
    public void shouldNotBookRoomForNullBookingRequest() {
        final String exceptionMessage = "Cannot perform reservation for empty reservation request. ";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(exceptionMessage);

        sut.performBooking(null);

        verify(byGuestAndDateDao, never()).save(any(RoomByGuestAndDate.class));
    }


    @Test
    public void shouldNotPerformBookingRequestIfSuchRoomDoesNotExists() {
        final Integer roomNumber = 1;
        final BookingRequest bookingRequest = getBookingRequest(roomNumber);
        final String exceptionMessage = format("The following room does not exists. Room number: '%d', hotel id: '%s'",
                bookingRequest.getRoomNumber(), bookingRequest.getHotelId());

        thrown.expect(RecordNotFoundException.class);
        thrown.expectMessage(exceptionMessage);

        sut.performBooking(bookingRequest);
    }

    @Test
    public void shouldThrowAlreadyBookedException() {
        final Integer roomNumber = 1;
        final BookingRequest bookingRequest = getBookingRequest(roomNumber);
        final RoomByGuestAndDate expectedRoomByGuestAndDate = new RoomByGuestAndDate(bookingRequest);
        final String exceptionMessage = format("The following room is already booked. Room number: '%d', hotel id: '%s'",
                bookingRequest.getRoomNumber(), bookingRequest.getHotelId());

        thrown.expect(RecordExistsException.class);
        thrown.expectMessage(exceptionMessage);

        when(byGuestAndDateDao.exists(eq(expectedRoomByGuestAndDate.getCompositeId()))).thenReturn(true);

        sut.performBooking(bookingRequest);

        verify(byGuestAndDateDao, never()).save(any(RoomByGuestAndDate.class));
    }


    private BookingRequest getBookingRequest(Integer roomNumber) {
        final UUID hotelId = randomUUID();
        final UUID guestId = randomUUID();
        final LocalDate bookingDate = now();

        final BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setBookingDate(bookingDate);
        bookingRequest.setGuestId(guestId);
        bookingRequest.setHotelId(hotelId);
        bookingRequest.setRoomNumber(roomNumber);
        return bookingRequest;
    }
}
