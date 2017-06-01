package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.dao.reservation.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.AvailableRoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.AvailableRoomByHotelAndDate;
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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
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
    private AvailableRoomByHotelAndDateDao availableRoomByHotelAndDateDao;
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
        final AvailableRoomByHotelAndDate expectedAvailableRoomByHotelAndDate = new AvailableRoomByHotelAndDate(hotelId, roomNumber, LocalDate.now());
        final Hotel hotel = new Hotel();

        when(availableRoomByHotelAndDateDao.insert(eq(expectedAvailableRoomByHotelAndDate))).thenReturn(expectedAvailableRoomByHotelAndDate);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(hotel);
        final AvailableRoomByHotelAndDate actualAvailableRoomByHotelAndDate = sut.addRoomToHotel(expectedAvailableRoomByHotelAndDate);

        verify(availableRoomByHotelAndDateDao).insert(eq(expectedAvailableRoomByHotelAndDate));
        assertEquals(expectedAvailableRoomByHotelAndDate, actualAvailableRoomByHotelAndDate);
    }

    @Test
    public void shouldNotAddNewRoomToUnknownHotel() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final AvailableRoomByHotelAndDate expectedAvailableRoomByHotelAndDate = new AvailableRoomByHotelAndDate(hotelId, roomNumber, LocalDate.now());
        final Hotel hotel = null;
        final String exceptionMessage = format("Cannot find the hotel for the given hotel id '%s'", expectedAvailableRoomByHotelAndDate.getId());

        when(hotelDao.findOne(eq(hotelId))).thenReturn(hotel);
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);
        final AvailableRoomByHotelAndDate actualAvailableRoomByHotelAndDate = sut.addRoomToHotel(expectedAvailableRoomByHotelAndDate);

        verify(availableRoomByHotelAndDateDao, never()).insert(any());
        assertEquals(expectedAvailableRoomByHotelAndDate, actualAvailableRoomByHotelAndDate);
    }

    @Test
    public void shouldNotAddTheSameRoomTwice() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final AvailableRoomByHotelAndDate expectedAvailableRoomByHotelAndDate = new AvailableRoomByHotelAndDate(hotelId, roomNumber, LocalDate.now());
        final String exceptionMessage = format("The room is already inserted in DB. Room info '%s'", expectedAvailableRoomByHotelAndDate);

        when(availableRoomByHotelAndDateDao.exists(eq(expectedAvailableRoomByHotelAndDate.getCompositeId()))).thenReturn(true);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(new Hotel());
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordExistsException.class);
        final AvailableRoomByHotelAndDate actualAvailableRoomByHotelAndDate = sut.addRoomToHotel(expectedAvailableRoomByHotelAndDate);

        verify(availableRoomByHotelAndDateDao, never()).insert(any());
        assertEquals(expectedAvailableRoomByHotelAndDate, actualAvailableRoomByHotelAndDate);
    }

    @Test
    public void shouldNotAddNullNewRoom() {
        final String exceptionMessage = "Cannot add the the room. It is empty. ";

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        sut.addRoomToHotel(null);

        verify(availableRoomByHotelAndDateDao, never()).insert(any());
        verify(hotelDao, never()).findOne(any(UUID.class));
    }

    @Test
    public void shouldNotAddNewRoomWithNullHotelId() {
        final UUID hotelId = null;
        final Integer roomNumber = 4;
        final AvailableRoomByHotelAndDate expectedAvailableRoomByHotelAndDate = new AvailableRoomByHotelAndDate(hotelId, roomNumber, LocalDate.now());
        final String exceptionMessage = format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                expectedAvailableRoomByHotelAndDate.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        sut.addRoomToHotel(expectedAvailableRoomByHotelAndDate);

        verify(availableRoomByHotelAndDateDao, never()).insert(any());
    }

    @Test
    public void shouldNotAddNewRoomWithEmptyRoomNumber() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 0;
        final AvailableRoomByHotelAndDate expectedAvailableRoomByHotelAndDate = new AvailableRoomByHotelAndDate(hotelId, roomNumber, LocalDate.now());
        final String exceptionMessage = format("Cannot add the the room with number '%d'. ", expectedAvailableRoomByHotelAndDate.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        when(hotelDao.findOne(eq(hotelId))).thenReturn(new Hotel());
        sut.addRoomToHotel(expectedAvailableRoomByHotelAndDate);

        verify(availableRoomByHotelAndDateDao, never()).insert(any());
    }

    @Test
    public void shouldThrowExceptionIfNoFreeRoomsWereFoundInTheHotel() {
        final UUID hotelId = UUID.randomUUID();
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusDays(3);
        final SearchRequest searchRequest = new SearchRequest(start, end, hotelId);

        final String exceptionMessage = format("No free rooms were found for the given request '%s'", searchRequest);
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);

        List<AvailableRoomByHotelAndDate> freeRoomsInTheHotel = sut.findFreeRoomsInTheHotel(searchRequest);
    }

    @Test
    public void shouldFindFreeRoomsInTheHotel() {
        final UUID hotelId = UUID.randomUUID();
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusDays(3);
        final SearchRequest searchRequest = new SearchRequest(start, end, hotelId);
        final List<AvailableRoomByHotelAndDate> rooms = buildRoomsInHotelAndDate(hotelId, start);
        final List<AvailableRoomByHotelAndDate> expectedFreeRooms = rooms.stream().filter(AvailableRoomByHotelAndDate::getAvailable).collect(toList());
        when(availableRoomByHotelAndDateDao.findAvailableRoomsForHotelId(eq(hotelId), eq(start), eq(end)))
                .thenReturn(rooms);

        List<AvailableRoomByHotelAndDate> actualFreeRoomsInTheHotel = sut.findFreeRoomsInTheHotel(searchRequest);

        verify(availableRoomByHotelAndDateDao).findAvailableRoomsForHotelId(eq(hotelId), eq(start), eq(end));
        assertFalse(actualFreeRoomsInTheHotel == null);
        assertTrue(actualFreeRoomsInTheHotel.size() == 2);
        assertTrue(actualFreeRoomsInTheHotel.containsAll(expectedFreeRooms));
        assertTrue(expectedFreeRooms.containsAll(actualFreeRoomsInTheHotel));
    }

    private List<AvailableRoomByHotelAndDate> buildRoomsInHotelAndDate(UUID hotelId, LocalDate localDate) {
        List<AvailableRoomByHotelAndDate> allRoomsInHotel = new ArrayList<>();

        AvailableRoomByHotelAndDate first = new AvailableRoomByHotelAndDate(hotelId, 1, localDate);
        AvailableRoomByHotelAndDate second = new AvailableRoomByHotelAndDate(hotelId, 2, localDate);
        AvailableRoomByHotelAndDate third = new AvailableRoomByHotelAndDate(hotelId, 2, localDate);
        third.setAvailable(false);
        allRoomsInHotel.add(first);
        allRoomsInHotel.add(second);
        allRoomsInHotel.add(third);
        return allRoomsInHotel;
    }
}
