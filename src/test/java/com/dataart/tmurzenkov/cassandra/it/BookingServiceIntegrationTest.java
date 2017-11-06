package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.TestUtils;
import com.dataart.tmurzenkov.cassandra.controller.GuestController;
import com.dataart.tmurzenkov.cassandra.controller.HotelController;
import com.dataart.tmurzenkov.cassandra.controller.RoomController;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.dataart.tmurzenkov.cassandra.service.impl.service.BookingServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;

import java.time.LocalDate;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildBookingRequest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildNewGuest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildAddress;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildHotel;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_GUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.ADD_HOTEL;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.ADD_ROOM;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link BookingServiceImpl}.
 *
 * @author tmurzenkov
 */
public class BookingServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ServiceResourceAssembler<Room, Class<RoomController>> resourceResourceAssemblerForRoom;
    @Autowired
    private ServiceResourceAssembler<Hotel, Class<HotelController>> resourceResourceAssemblerForHotel;
    @Autowired
    private ServiceResourceAssembler<Guest, Class<GuestController>> resourceResourceAssembler;
    @Test
    public void shouldPerformBookingRequest() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final Integer roomNumber = 2;
        final Guest guest = buildNewGuest(guestId);
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final LocalDate bookingRequestDate = LocalDate.now();
        final Room expectedRoom = TestUtils.RoomTestUtils.buildRoom(hotelId, roomNumber);
        final BookingRequest bookingRequest = buildBookingRequest(hotelId, guestId, roomNumber, bookingRequestDate);

        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<Guest> guestResource = resourceResourceAssembler
                .withController(GuestController.class)
                .toResource(guest);
        final Resource<Room> roomResource = resourceResourceAssemblerForRoom
                .withController(RoomController.class)
                .toResource(expectedRoom);


        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(expectedRoom)).contentType(APPLICATION_JSON_UTF8))
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
        final Room expectedRoomByHotelAndDateToAdd = TestUtils.RoomTestUtils.buildRoom(hotelId, roomNumber);
        final String message = format("The following room is already booked. Room number: '%d', hotel id: '%s'",
                roomNumber, hotelId);
        final RuntimeException exception = new IllegalArgumentException(message);
        final Resource<Hotel> hotelResource = resourceResourceAssemblerForHotel
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);
        final Resource<Guest> guestResource = resourceResourceAssembler
                .withController(GuestController.class)
                .toResource(guest);
        final Resource<Room> roomResource = resourceResourceAssemblerForRoom
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

}
