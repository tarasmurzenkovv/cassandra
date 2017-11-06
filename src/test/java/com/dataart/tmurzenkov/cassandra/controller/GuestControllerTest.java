package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.dataart.tmurzenkov.cassandra.service.impl.service.GuestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildNewGuest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRooms;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_GUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ROOMS_BY_GUEST_AND_DATE;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.QUERY_EXECUTION_EXCEPTION;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static com.dataart.tmurzenkov.cassandra.util.DateUtils.format;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link GuestController}.
 *
 * @author tmurzenkov
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestControllerTest extends AbstractControllerUnitTest<GuestController> {
    @Mock
    private GuestServiceImpl guestService;
    @Mock
    private ServiceResourceAssembler<Guest, Resource<Guest>> resourceAssembler;
    @InjectMocks
    private GuestController sut;
    private MockMvc mockMvc;

    /**
     * Inits {@link MockMvc}.
     */
    @Before
    public void init() {
        this.mockMvc = this.init(sut);
    }

    @Test
    public void shouldRegisterANewGuest() throws Exception {
        final Guest guest = buildNewGuest(randomUUID());
        final Resource<Guest> guestResource = new Resource<>(guest);

        when(guestService.registerNewGuest(eq(guest))).thenReturn(guest);
        when(resourceAssembler.withController(eq(GuestController.class))).thenReturn(resourceAssembler);
        when(resourceAssembler.toResource(eq(guest))).thenReturn(guestResource);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(guestResource)));
    }

    @Test
    public void shouldNotRegisterTheSameGuestTwice() throws Exception {
        final Guest guest = buildNewGuest(randomUUID());
        final String exceptionMessage = format("The guest information is already stored in DB. "
                + "Guest id: '%s', name: '%s', surname: '%s'", guest.getId(), guest.getFirstName(), guest.getLastName());
        final RuntimeException exception = new RecordExistsException(exceptionMessage);

        when(guestService.registerNewGuest(eq(guest))).thenThrow(exception);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, BAD_REQUEST).getBody())));
    }

    @Test
    public void shouldNotRegisterGuestWithEmptyId() throws Exception {
        final Guest guest = buildNewGuest(null);
        final String exceptionMessage = "Cannot register guest info with empty id. ";
        final RuntimeException exception = new IllegalArgumentException(exceptionMessage);

        when(guestService.registerNewGuest(eq(guest))).thenThrow(exception);
        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(asJson(build(exception, QUERY_EXECUTION_EXCEPTION, BAD_REQUEST).getBody())));
    }

    @Test
    public void shouldNotRegisterGuestWithEmptyFirstName() throws Exception {
        final Guest guest = buildNewGuest(UUID.randomUUID());
        guest.setFirstName("");
        final String exceptionMessage = "Cannot register guest info with empty first name. ";
        final RuntimeException exception = new IllegalArgumentException(exceptionMessage);

        when(guestService.registerNewGuest(eq(guest))).thenThrow(exception);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(asJson(build(exception, QUERY_EXECUTION_EXCEPTION, BAD_REQUEST).getBody())));
    }

    @Test
    public void shouldNotRegisterGuestWithEmptyLastName() throws Exception {
        final Guest guest = buildNewGuest(UUID.randomUUID());
        guest.setLastName("");
        final String exceptionMessage = "Cannot register guest info with empty last name. ";
        final RuntimeException exception = new IllegalArgumentException(exceptionMessage);

        when(guestService.registerNewGuest(eq(guest))).thenThrow(exception);

        mockMvc
                .perform(post(ADD_GUEST).content(asJson(guest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(asJson(build(exception, QUERY_EXECUTION_EXCEPTION, BAD_REQUEST).getBody())));
    }

    @Test
    public void shouldLookForBookedRoomsForCustomerIdAndBookingDate() throws Exception {
        final UUID guestId = UUID.randomUUID();
        final LocalDate bookingDate = now();
        List<RoomByHotelAndDate> roomByHotelAndDates = buildRooms(10);
        when(guestService.findBookedRoomsForTheGuestIdAndDate(eq(guestId), eq(bookingDate))).thenReturn(roomByHotelAndDates);
        mockMvc
                .perform(get(ROOMS_BY_GUEST_AND_DATE, guestId, format(bookingDate)))
                .andExpect(status().isFound())
                .andExpect(content().string(asJson(roomByHotelAndDates)));
    }
}
