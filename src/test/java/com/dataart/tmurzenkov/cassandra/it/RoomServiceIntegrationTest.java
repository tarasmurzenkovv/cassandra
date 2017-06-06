package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.controller.HotelController;
import com.dataart.tmurzenkov.cassandra.controller.RoomController;

import com.dataart.tmurzenkov.cassandra.dao.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.impl.service.RoomServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildBookingRequest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildNewGuest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildSearchRequest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildAddress;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildHotel;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRoom;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRooms;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_GUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.ADD_HOTEL;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.ADD_ROOM;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.GET_FREE_ROOMS;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_NOT_EXISTS;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link RoomServiceImpl}.
 *
 * @author tmurzenkov
 */
public class RoomServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private GuestDao guestDao;
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Autowired
    private HotelByCityDao hotelByCityDao;
    @Autowired
    private RoomByGuestAndDateDao roomByGuestAndDateDao;

    @Autowired
    private ServiceResourceAssembler<RoomByHotelAndDate, Class<RoomController>> resourceResourceAssemblerForRoom;
    @Autowired
    private ServiceResourceAssembler<Hotel, Class<HotelController>> resourceResourceAssemblerForHotel;

    @Override
    public void clearDbEntries() {
        guestDao.deleteAll();
        hotelDao.deleteAll();
        hotelByCityDao.deleteAll();
        roomByHotelAndDateDao.deleteAll();
        roomByGuestAndDateDao.deleteAll();
    }

    @Test
    public void shouldAddNewRoom() throws Exception {
        final Integer roomNumber = 2;
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final RoomByHotelAndDate expectedRoomByHotelAndDateToAdd = buildRoom(hotelId, roomNumber, LocalDate.now());
        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<RoomByHotelAndDate> roomResource = resourceResourceAssemblerForRoom
                .withController(RoomController.class)
                .toResource(expectedRoomByHotelAndDateToAdd);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(roomResource)));
    }

    @Test
    public void shouldNotAddTwiceTheSameRoom() throws Exception {
        final Integer roomNumber = 2;
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final RoomByHotelAndDate expectedRoomByHotelAndDateToAdd = buildRoom(hotelId, roomNumber, LocalDate.now());
        final String message = format("The room is already inserted in DB. Room info '%s'", expectedRoomByHotelAndDateToAdd);
        final RuntimeException exception = new RecordExistsException(message);

        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<RoomByHotelAndDate> roomResource = resourceResourceAssemblerForRoom
                .withController(RoomController.class)
                .toResource(expectedRoomByHotelAndDateToAdd);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(roomResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, CONFLICT).getBody())));
    }

    @Test
    public void shouldNotAddTheRoomToUnknownHotel() throws Exception {
        final Integer roomNumber = 2;
        final UUID hotelId = UUID.randomUUID();
        final RoomByHotelAndDate expectedRoomByHotelAndDateToAdd = buildRoom(hotelId, roomNumber, LocalDate.now());
        final String message = format("Cannot find the hotel for the given hotel id '%s'", hotelId);
        final RuntimeException exception = new RecordExistsException(message);

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(asJson(build(exception, RECORD_NOT_EXISTS, NOT_FOUND).getBody())));
    }

    @Test
    public void shouldFindAllFreeRoomsInHotel() throws Exception {
        final int daysToAdd = 6;
        final Integer numberOfRoomsToGenerate = 2;
        final LocalDate localDateBookingRequest = LocalDate.now();
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final UUID guestId = UUID.randomUUID();
        final Guest guest = buildNewGuest(guestId);
        final List<RoomByHotelAndDate> roomsToAdd = buildRooms(numberOfRoomsToGenerate, hotelId, localDateBookingRequest);
        final RoomByHotelAndDate roomByHotelAndDateToBook = roomsToAdd.get(0);
        final Integer roomNumber = roomByHotelAndDateToBook.getRoomNumber();
        final BookingRequest bookingRequest = buildBookingRequest(hotelId, guestId, roomNumber, localDateBookingRequest);
        final SearchRequest searchRequest = buildSearchRequest(daysToAdd, hotelId);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        roomsToAdd.forEach(room -> {
            try {
                mockMvc
                        .perform(post(ADD_ROOM).content(asJson(room)).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        roomsToAdd.remove(roomByHotelAndDateToBook);

        mockMvc
                .perform(post(GET_FREE_ROOMS).content(asJson(searchRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}
