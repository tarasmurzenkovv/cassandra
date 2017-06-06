package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.impl.service.RoomServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.validation.RoomValidatorServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildRoomsInHotelAndDate;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;


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
    private RoomValidatorServiceImpl validatorService;
    @Mock
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Mock
    private HotelDao hotelDao;
    @Mock
    private RoomDao roomDao;
    @InjectMocks
    private RoomServiceImpl sut;

    @Test
    public void shouldAddNewRoomToHotel() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);

        doNothing().when(validatorService).validateInfo(eq(expectedRoom));
        doNothing().when(validatorService).checkIfExists(eq(expectedRoom));
        when(roomDao.insert(eq(expectedRoom))).thenReturn(expectedRoom);

        final Room actualAddedRoom = sut.addRoomToHotel(expectedRoom);
        verify(validatorService).validateInfo(eq(expectedRoom));
        verify(validatorService).checkIfExists(eq(expectedRoom));
        verify(roomDao).insert(eq(expectedRoom));
        assertEquals(expectedRoom, actualAddedRoom);
    }

    @Test
    public void shouldNotAddNewRoomToUnknownHotel() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("Cannot find the hotel for the given hotel id '%s'", expectedRoom.getId());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);
        doThrow(new RecordNotFoundException(exceptionMessage)).when(validatorService).validateInfo(eq(expectedRoom));
        final Room actualRoomByHotelAndDate = sut.addRoomToHotel(expectedRoom);

        verify(roomByHotelAndDateDao, never()).insert(any());
        verify(validatorService).checkIfExists(eq(expectedRoom));
        assertEquals(expectedRoom, actualRoomByHotelAndDate);
    }

    @Test
    public void shouldNotAddTheSameRoomTwice() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 4;
        final Room expectedRoom = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("The room is already inserted in DB. Room info '%s'", expectedRoom);

        doThrow(new RecordExistsException(exceptionMessage)).when(validatorService).checkIfExists(eq(expectedRoom));

        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordExistsException.class);
        final Room actualRoomByHotelAndDate = sut.addRoomToHotel(expectedRoom);

        verify(roomByHotelAndDateDao, never()).insert(any());
        verify(validatorService).checkIfExists(eq(expectedRoom));
        assertEquals(expectedRoom, actualRoomByHotelAndDate);
    }

    @Test
    public void shouldNotAddNullNewRoom() {
        final String exceptionMessage = "Cannot add the the room. It is empty. ";

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        final Room expectedRoom = null;
        doThrow(new IllegalArgumentException(exceptionMessage)).when(validatorService).validateInfo(eq(expectedRoom));
        sut.addRoomToHotel(expectedRoom);

        verify(roomByHotelAndDateDao, never()).insert(any());
        verify(hotelDao, never()).findOne(any(UUID.class));
    }

    @Test
    public void shouldNotAddNewRoomWithNullHotelId() {
        final UUID hotelId = null;
        final Integer roomNumber = 4;
        final Room expectedRoomByHotelAndDate = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                expectedRoomByHotelAndDate.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        doThrow(new IllegalArgumentException(exceptionMessage)).when(validatorService).validateInfo(eq(expectedRoomByHotelAndDate));
        sut.addRoomToHotel(expectedRoomByHotelAndDate);

        verify(roomByHotelAndDateDao, never()).insert(any());
    }

    @Test
    public void shouldNotAddNewRoomWithEmptyRoomNumber() {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 0;
        final Room expectedRoomByHotelAndDate = new Room(hotelId, roomNumber);
        final String exceptionMessage = format("Cannot add the the room with number '%d'. ", expectedRoomByHotelAndDate.getRoomNumber());

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);
        doThrow(new IllegalArgumentException(exceptionMessage)).when(validatorService).validateInfo(eq(expectedRoomByHotelAndDate));
        sut.addRoomToHotel(expectedRoomByHotelAndDate);

        verify(roomByHotelAndDateDao, never()).insert(any());
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

        Set<Room> freeRoomsInTheHotel = sut.findFreeRoomsInTheHotel(searchRequest);
    }

    @Test
    public void shouldFindFreeRoomsInTheHotel() {
        final UUID hotelId = UUID.randomUUID();
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusDays(3);
        final SearchRequest searchRequest = new SearchRequest(start, end, hotelId);
        final Set<RoomByHotelAndDate> roomsByHotelAndDate = buildRoomsInHotelAndDate(hotelId, start, 3);
        Set<Room> bookedRooms = roomsByHotelAndDate.stream().map(Room::new).collect(toSet());
        Set<Room> allRooms = buildRoomsForHotel(hotelId, 6);

        when(roomDao.findAllRoomsByHotelId(eq(hotelId))).thenReturn(allRooms);
        when(roomByHotelAndDateDao.findAllRoomsForHotelIdAndPeriod(eq(hotelId), eq(start), eq(end)))
                .thenReturn(roomsByHotelAndDate);

        final Set<Room> freeRoomsInTheHotel = sut.findFreeRoomsInTheHotel(searchRequest);
        allRooms.removeAll(bookedRooms);

        assertEquals(allRooms.size(), freeRoomsInTheHotel.size());
        assertTrue(allRooms.containsAll(freeRoomsInTheHotel));
        assertTrue(freeRoomsInTheHotel.containsAll(allRooms));
    }

    private Set<Room> buildRoomsForHotel(final UUID hotelId, final int i) {
        return IntStream.range(1, i + 1).mapToObj(idx -> new Room(hotelId, idx)).collect(toSet());
    }
}
