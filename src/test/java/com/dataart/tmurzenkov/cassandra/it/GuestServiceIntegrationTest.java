package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.controller.GuestController;
import com.dataart.tmurzenkov.cassandra.controller.HotelController;
import com.dataart.tmurzenkov.cassandra.controller.RoomController;
import com.dataart.tmurzenkov.cassandra.dao.reservation.RoomByGuestAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.service.impl.GuestServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildBookingRequest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildNewGuest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildAddress;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildHotel;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRoom;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRooms;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.TestUtils.makeList;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_GUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ROOMS_BY_GUEST_AND_DATE;
import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.ADD_HOTEL;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.ADD_ROOM;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static com.dataart.tmurzenkov.cassandra.service.util.DateUtils.format;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link GuestServiceImpl}.
 *
 * @author tmurzenkov
 */
public class GuestServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ServiceResourceAssembler<Guest, Class<GuestController>> resourceResourceAssembler;
    @Autowired
    private ServiceResourceAssembler<Hotel, Class<HotelController>> resourceResourceAssemblerForHotel;
    @Autowired
    private ServiceResourceAssembler<RoomByHotelAndDate, Class<RoomController>> resourceResourceAssemblerForRoom;
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

    /**
     * Clear all db entries.
     */
    public void clearDbEntries() {
        guestDao.deleteAll();
        hotelDao.deleteAll();
        hotelByCityDao.deleteAll();
        roomByHotelAndDateDao.deleteAll();
        roomByGuestAndDateDao.deleteAll();
    }

    @Test
    public void shouldAddANewGuest() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final Guest guest = buildNewGuest(guestId);
        final Resource<Guest> guestResource = resourceResourceAssembler.withController(GuestController.class).toResource(guest);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));

        assertEquals(guestDao.findOne(guest.getCompositeId()), guest);
    }

    @Test
    public void shouldNotAddTwiceTheSameGuest() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final Guest guest = buildNewGuest(guestId);
        final String message = format("The guest information is already stored in DB. "
                + "Guest id: '%s', name: '%s', surname: '%s'", guest.getId(), guest.getFirstName(), guest.getLastName());
        final RuntimeException exception = new IllegalArgumentException(message);
        final Resource<Guest> guestResource = resourceResourceAssembler.withController(GuestController.class).toResource(guest);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));

        assertEquals(guestDao.findOne(guest.getCompositeId()), guest);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, CONFLICT).getBody())));

        List<Guest> foundGuests = makeList(guestDao.findAll());
        assertEquals(foundGuests.size(), 1);
        assertTrue(foundGuests.contains(guest));
    }

    @Test
    public void shouldPerformBookingRequest() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final Integer roomNumber = 2;
        final Guest guest = buildNewGuest(guestId);
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final LocalDate bookingRequestDate = LocalDate.now();
        final RoomByHotelAndDate expectedRoomByHotelAndDateToAdd = buildRoom(hotelId, roomNumber, bookingRequestDate);
        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<Guest> guestResource = resourceResourceAssembler
                .withController(GuestController.class)
                .toResource(guest);
        final Resource<RoomByHotelAndDate> roomResource = resourceResourceAssemblerForRoom
                .withController(RoomController.class)
                .toResource(expectedRoomByHotelAndDateToAdd);
        final BookingRequest bookingRequest = buildBookingRequest(hotelId, guestId, roomNumber, bookingRequestDate);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(roomResource)));

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(new Resource<>(bookingRequest))));
    }

    @Test
    public void shouldNotBookTwiceTheSameRoom() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final Integer roomNumber = 2;
        final Guest guest = buildNewGuest(guestId);
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final LocalDate bookingRequestDate = LocalDate.now();
        final RoomByHotelAndDate expectedRoomByHotelAndDateToAdd = buildRoom(hotelId, roomNumber, bookingRequestDate);
        final String message = format("The following room is already booked. Room number: '%d', hotel id: '%s'",
                roomNumber, hotelId);
        final RuntimeException exception = new IllegalArgumentException(message);
        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<Guest> guestResource = resourceResourceAssembler
                .withController(GuestController.class)
                .toResource(guest);
        final Resource<RoomByHotelAndDate> roomResource = resourceResourceAssemblerForRoom
                .withController(RoomController.class)
                .toResource(expectedRoomByHotelAndDateToAdd);
        final BookingRequest bookingRequest = buildBookingRequest(hotelId, guestId, roomNumber, bookingRequestDate);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoomByHotelAndDateToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(roomResource)));

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(new Resource<>(bookingRequest))));

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, BAD_REQUEST).getBody())));
    }

    @Test
    public void shouldFindAllBookedRooms() throws Exception {
        final int numberToBuild = 10;
        final LocalDate bookingDate = now();
        final UUID guestId = UUID.randomUUID();
        final Guest guest = buildNewGuest(guestId);
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final List<RoomByHotelAndDate> roomByHotelAndDates = buildRooms(numberToBuild, hotelId);
        final List<BookingRequest> bookingRequests = roomByHotelAndDates
                .stream()
                .map(room -> new BookingRequest(guestId, hotelId, room.getRoomNumber(), bookingDate))
                .collect(toList());
        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<Guest> guestResource = resourceResourceAssembler
                .withController(GuestController.class)
                .toResource(guest);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));
        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));
        roomByHotelAndDates.forEach(room -> {
            try {
                mockMvc
                        .perform(post(ADD_ROOM).content(asJson(room)).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        bookingRequests.forEach(bookingRequest -> {
            try {
                mockMvc
                        .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().string(asJson(new Resource<>(bookingRequest))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        mockMvc.perform(get(ROOMS_BY_GUEST_AND_DATE, guestId, format(bookingDate)))
                .andExpect(status().isFound()).andExpect(content().string(asJson(roomByHotelAndDates)));
    }
}
