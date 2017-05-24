package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * ReservationByGuestId controller.
 * <p>
 * - "api"/"add"/"booking"
 * - "api"/"add"/"guest"
 * - "api"/"get"/"roombyguest"
 *
 * @author tmurzenkov
 */
@RestController
@Api(description = "REST API to manage hotel guests in the booking system. ")
public class GuestController {
    @Autowired
    private ResourceAssembler<Guest, Resource<Guest>> resourceAssembler;
    @Autowired
    private GuestService guestService;

    /**
     * Registers the new {@link Guest} in the hotel system.
     *
     * @param guest {@link Guest}
     * @return {@link Resource}
     */
    @ApiOperation(value = "Adds new hotel guest to the system.",
            notes = "Adds new hotel guest to the system and returns the location header. ")
    @RequestMapping(path = "/api/add/guest", method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Resource<Guest> registerNewGuest(@RequestBody Guest guest) {
        guestService.registerNewGuest(guest);
        return resourceAssembler.toResource(guest);
    }

    /**
     * Books the room by the registered user id, hotel id, start date, end date, room number.
     *
     * @param bookingRequest {@link BookingRequest} the id of the hotel
     */
    @ApiOperation(value = "Books the room.",
            notes = "Books the room by the registered user id, hotel id, start date, end date, room number.")
    @RequestMapping(path = "/api/add/booking", method = POST)
    @ResponseStatus(OK)
    public void bookRoom(@RequestBody BookingRequest bookingRequest) {
        guestService.performBooking(bookingRequest);
    }

    /**
     * Finds all booked rooms for the given guest id and date.
     *
     * @param guestId       {@link UUID}
     * @param dateToLookFor {@link Date}
     * @return {@link List} of {@link Room}
     */
    @ApiOperation(value = "Gets booked rooms for the guest id and specific date. ",
            notes = "Gets booked rooms for the guest id and specific date. ")
    @RequestMapping(path = "/api/get/roombyguest/{guestId}/{date}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(FOUND)
    public List<Room> bookedRoomsByGuest(
            @ApiParam(required = true, value = "The UUID representation of the guest id. ")
            @PathVariable("guestId") UUID guestId,
            @PathVariable("date")
            @ApiParam(required = true, value = "Specific date to look at the booked rooms. ")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateToLookFor) {
        return guestService.findBookedRoomsForTheGuestIdAndDate(guestId, dateToLookFor);
    }
}
