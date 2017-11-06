package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.service.impl.service.BookingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Resource;
import org.springframework.test.web.servlet.MockMvc;

import static com.dataart.tmurzenkov.cassandra.TestUtils.GuestTestUtils.buildBookingRequest;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UTs for the  {@link BookingController}.
 *
 * @author tmurzenkov
 */
@RunWith(MockitoJUnitRunner.class)
public class BookingControllerTest extends AbstractControllerUnitTest<BookingController> {
    @Mock
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingController sut;
    private MockMvc mockMvc;

    /**
     * Creates and initialize the {@link MockMvc}.
     */
    @Before
    public void init() {
        this.mockMvc = this.init(sut);
    }

    @Test
    public void shouldExecuteBookingRequest() throws Exception {
        final BookingRequest bookingRequest = buildBookingRequest();
        final Resource<BookingRequest> bookingRequestResource = new Resource<>(bookingRequest);

        when(bookingService.performBooking(eq(bookingRequest))).thenReturn(bookingRequest);

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(bookingRequestResource)));
    }

    @Test
    public void shouldNotExecuteBookingRequestForEmptyHotelId() throws Exception {
        final BookingRequest bookingRequest = buildBookingRequest();
        final String expectedContent =
                "{\"exceptionMessage\":\"The hotel id must not be null. \",\"exceptionDescription\":\"INVALID_PARAMETERS\"}";
        bookingRequest.setHotelId(null);

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void shouldNotExecuteBookingRequestForEmptyRoomNumber() throws Exception {
        final BookingRequest bookingRequest = buildBookingRequest();
        final String expectedContent =
                "{\"exceptionMessage\":\"The room number must not be null. \",\"exceptionDescription\":\"INVALID_PARAMETERS\"}";
        bookingRequest.setRoomNumber(null);

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void shouldNotExecuteBookingRequestForEmptyBookingDate() throws Exception {
        final BookingRequest bookingRequest = buildBookingRequest();
        final String expectedContent =
                "{\"exceptionMessage\":\"The reservation date must not be null. \",\"exceptionDescription\":\"INVALID_PARAMETERS\"}";
        bookingRequest.setBookingDate(null);

        mockMvc
                .perform(post(ADD_BOOKING).content(asJson(bookingRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));
    }

}
