package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.impl.RoomServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus.BOOKED;
import static com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus.RESERVED;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UTs for the {@link RoomServiceTest}.
 *
 * @author tmurzenkov
 */
@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private RoomDao roomDao;
    @Mock
    private HotelDao hotelDao;
    @Mock
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @InjectMocks
    private RoomServiceImpl sut;

    @Test
    public void shouldAddNewRoomToHotel() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final Hotel hotel = new Hotel();

        when(roomDao.insert(eq(expectedRoom))).thenReturn(expectedRoom);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(hotel);
        final Room actualRoom = sut.addRoomToHotel(expectedRoom);

        verify(roomDao).insert(eq(expectedRoom));
        assertEquals(expectedRoom, actualRoom);
    }

    @Test
    public void shouldNotAddNewRoomToUnknownHotel() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final Hotel hotel = null;
        final String exceptionMessage = format("Cannot find the hotel for the given hotel id '%s'", expectedRoom.getId());

        when(hotelDao.findOne(eq(hotelId))).thenReturn(hotel);
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);
        final Room actualRoom = sut.addRoomToHotel(expectedRoom);

        verify(roomDao, never()).insert(any());
        assertEquals(expectedRoom, actualRoom);
    }

    @Test
    public void shouldNotAddTheSameRoomTwice() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("The room is already inserted in DB. Room info '%s'", expectedRoom);

        when(roomDao.exists(eq(expectedRoom.getCompositeId()))).thenReturn(true);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(new Hotel());
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordExistsException.class);
        final Room actualRoom = sut.addRoomToHotel(expectedRoom);

        verify(roomDao, never()).insert(any());
        assertEquals(expectedRoom, actualRoom);
    }

    @Test
    public void shouldNotAddNullNewRoom() {
        final String exceptionMessage = "Cannot add the the room. It is empty. ";

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        sut.addRoomToHotel(null);

        verify(roomDao, never()).insert(any());
        verify(hotelDao, never()).findOne(any(UUID.class));
    }

    @Test
    public void shouldNotAddNewRoomWithNullHotelId() {
        final UUID hotelId = null;
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                expectedRoom.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        sut.addRoomToHotel(expectedRoom);

        verify(roomDao, never()).insert(any());
    }

    @Test
    public void shouldNotAddNewRoomWithEmptyRoomNumber() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 0;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("Cannot add the the room with number '%d'. ", expectedRoom.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(new Hotel());
        sut.addRoomToHotel(expectedRoom);

        verify(roomDao, never()).insert(any());
    }

    @Test
    public void shouldThrowExceptionIfNoFreeRoomsWereFoundInTheHotel() {
        final UUID hotelId = UUID.randomUUID();
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusDays(3);
        final SearchRequest searchRequest = new SearchRequest(start, end, hotelId);

        when(roomByHotelAndDateDao.findBookedRoomInHotelForDate(hotelId, start, end)).thenReturn(emptyList());
        final String exceptionMessage = format("No free rooms were found for the given request '%s'", searchRequest);

        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);
        sut.findFreeRoomsInTheHotel(searchRequest);
    }

    @Test
    public void shouldFindFreeRoomsInTheHotel() {
        final UUID hotelId = UUID.randomUUID();
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusDays(3);
        final SearchRequest searchRequest = new SearchRequest(start, end, hotelId);
        final List<RoomByHotelAndDate> roomByHotelAndDates = buildRoomsByHotelAndDate(hotelId, start);
        final List<Room> expectedFreeRoomsInTheHotel = roomByHotelAndDates.stream()
                .filter(roomByHotelAndDate -> BOOKED != roomByHotelAndDate.getStatus())
                .map(Room::new)
                .collect(Collectors.toList());
        when(roomByHotelAndDateDao.findBookedRoomInHotelForDate(hotelId, start, end)).thenReturn(roomByHotelAndDates);

        List<Room> actualFreeRoomsInTheHotel = sut.findFreeRoomsInTheHotel(searchRequest);

        verify(roomByHotelAndDateDao).findBookedRoomInHotelForDate(eq(hotelId), eq(start), eq(end));
        assertFalse(actualFreeRoomsInTheHotel.isEmpty());
        assertEquals(actualFreeRoomsInTheHotel.size(), expectedFreeRoomsInTheHotel.size());
        assertTrue(actualFreeRoomsInTheHotel.containsAll(expectedFreeRoomsInTheHotel));
        assertTrue(expectedFreeRoomsInTheHotel.containsAll(actualFreeRoomsInTheHotel));
    }

    private List<RoomByHotelAndDate> buildRoomsByHotelAndDate(UUID hotelId, LocalDate localDate) {
        List<RoomByHotelAndDate> roomByHotelAndDates = new ArrayList<>();

        RoomByHotelAndDate first = new RoomByHotelAndDate();
        first.setStatus(RESERVED);
        first.setRoomNumber(1);
        first.setBookingDate(localDate);
        first.setId(hotelId);

        RoomByHotelAndDate second = new RoomByHotelAndDate();
        first.setStatus(BOOKED);
        second.setRoomNumber(2);
        second.setBookingDate(localDate);
        second.setId(hotelId);

        RoomByHotelAndDate third = new RoomByHotelAndDate();
        third.setStatus(BOOKED);
        third.setRoomNumber(3);
        third.setBookingDate(localDate);
        third.setId(hotelId);

        roomByHotelAndDates.add(first);
        roomByHotelAndDates.add(second);
        roomByHotelAndDates.add(third);
        return roomByHotelAndDates;
    }
}
