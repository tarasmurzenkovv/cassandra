package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.dataart.tmurzenkov.cassandra.service.impl.service.RoomServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.RoomTestUtils.buildRoom;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.ADD_ROOM;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.GET_FREE_ROOMS;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.QUERY_EXECUTION_EXCEPTION;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static java.lang.String.format;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link RoomController} UTs.
 *
 * @author tmurzenkov
 */
@RunWith(MockitoJUnitRunner.class)
public class RoomControllerTest extends AbstractControllerUnitTest<RoomController> {
    @Mock
    private RoomServiceImpl roomService;
    @Mock
    private ServiceResourceAssembler<Room, Resource<Room>> serviceResourceAssembler;
    @InjectMocks
    private RoomController sut;
    private MockMvc mockMvc;

    /**
     * Inits mock mvc.
     */
    @Before
    public void init() {
        this.mockMvc = this.init(sut);
    }

    @Test
    public void shouldAddRoom() throws Exception {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 5;
        final Room room = buildRoom(hotelId, roomNumber);
        final Resource<Room> roomResource = new Resource<>(room);

        when(roomService.addRoomToHotel(eq(room))).thenReturn(room);
        when(serviceResourceAssembler.withController(eq(sut.getClass()))).thenReturn(serviceResourceAssembler);
        when(serviceResourceAssembler.toResource(eq(room))).thenReturn(roomResource);

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(room)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(roomResource)));

        verify(roomService).addRoomToHotel(eq(room));
        verify(serviceResourceAssembler).withController(eq(sut.getClass()));
        verify(serviceResourceAssembler).toResource(eq(room));
    }

    @Test
    public void shouldNotAddRoomWithoutHotelId() throws Exception {
        final UUID hotelId = null;
        final Integer roomNumber = 5;
        final Room room = buildRoom(hotelId, roomNumber);
        final String nullHotelId = format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                roomNumber);
        final RuntimeException exception = new IllegalArgumentException(nullHotelId);
        when(roomService.addRoomToHotel(eq(room))).thenThrow(exception);

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(room)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(asJson(build(exception, QUERY_EXECUTION_EXCEPTION, BAD_REQUEST).getBody())));

        verify(roomService).addRoomToHotel(eq(room));
        verify(serviceResourceAssembler, never()).withController(eq(sut.getClass()));
        verify(serviceResourceAssembler, never()).toResource(eq(room));
    }

    @Test
    public void shouldNotAddTheSameRoomTwice() throws Exception {
        final UUID hotelId = UUID.randomUUID();
        final Integer roomNumber = 5;
        final Room room = buildRoom(hotelId, roomNumber);
        final RuntimeException exception = new RecordExistsException(format("The room is already inserted in DB. Room info '%s'", room));

        when(roomService.addRoomToHotel(eq(room))).thenThrow(exception);

        mockMvc
                .perform(post(ADD_ROOM).content(asJson(room)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, BAD_REQUEST).getBody())));

        verify(roomService).addRoomToHotel(eq(room));
        verify(serviceResourceAssembler, never()).withController(eq(sut.getClass()));
        verify(serviceResourceAssembler, never()).toResource(eq(room));
    }

    @Test
    public void shouldNotFindAllBookedRoomsForReverseStartEnd() throws Exception {
        final SearchRequest searchRequest = new SearchRequest();
        mockMvc
                .perform(post(GET_FREE_ROOMS).content(asJson(searchRequest)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
