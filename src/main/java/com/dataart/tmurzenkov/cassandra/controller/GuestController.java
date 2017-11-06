package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.service.BookingService;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_GUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ROOMS_BY_GUEST_AND_DATE;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.BAD_REQUEST;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.CONFLICT;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.NOT_FOUND;
import static com.dataart.tmurzenkov.cassandra.util.DateUtils.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Guest controller.
 *
 * @author tmurzenkov
 */
@RestController
@Api(value = "Guest operations", description = "REST API to manage hotel guests in the reservation system. ")
public class GuestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestController.class);
    private final ServiceResourceAssembler<Guest, Class<GuestController>> resourceAssembler;
    private final GuestService guestService;
    private final BookingService bookingService;

    /**
     * Autowire the below services into the controller.
     *
     * @param resourceAssembler {@link ServiceResourceAssembler}
     * @param guestService      {@link GuestService}
     * @param bookingService    {@link BookingService}
     */
    public GuestController(ServiceResourceAssembler<Guest, Class<GuestController>> resourceAssembler,
                           GuestService guestService,
                           BookingService bookingService) {
        this.resourceAssembler = resourceAssembler;
        this.guestService = guestService;
        this.bookingService = bookingService;
    }

    /**
     * Registers the new {@link Guest} in the hotel system.
     *
     * @param guest {@link Guest}
     * @return {@link Resource}
     */
    @ApiOperation(value = "Adds new hotel guest to the system.",
            notes = "Adds new hotel guest to the system and returns the location header. ")
    @RequestMapping(path = ADD_GUEST, method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @ApiResponses({
            @ApiResponse(code = HttpStatus.CREATED, message = "Registered a new guest information. "),
            @ApiResponse(code = HttpStatus.CONFLICT, message = "Such guest info is already present in DB. "),
            @ApiResponse(code = HttpStatus.BAD_REQUEST, message = "Some arguments are invalid in guest info dto. "),
    })
    public Resource<Guest> registerNewGuest(@RequestBody Guest guest) {
        LOGGER.info("Registering a new guest '{}'", guest);
        final Guest registeredGuest = guestService.registerNewGuest(guest);
        return resourceAssembler.withController(GuestController.class).toResource(registeredGuest);
    }

    /**
     * Books the room by the registered user id, hotel id, start date, end date, room number.
     *
     * @param bookingRequest {@link BookingRequest} the id of the hotel
     * @return {@link Resource}
     */
    @ApiOperation(value = "Books the room.",
            notes = "Books the room by the registered user id, hotel id, start date, end date, room number.")
    @RequestMapping(path = ADD_BOOKING, method = POST, consumes = "application/json", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    @ApiResponses({
            @ApiResponse(code = HttpStatus.CREATED, message = "The room has been booked successfully. "),
            @ApiResponse(code = CONFLICT, message = "The room is already booked. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    public Resource<BookingRequest> bookRoom(@RequestBody @Valid BookingRequest bookingRequest) {
        LOGGER.info("New reservation request is issued '{}'", bookingRequest);
        return new Resource<>(bookingService.performBooking(bookingRequest));
    }

    /**
     * Finds all booked rooms for the given guest id and date.
     *
     * @param guestId       {@link UUID}
     * @param dateToLookFor {@link Date}
     * @return {@link List} of {@link RoomByHotelAndDate}
     */
    @ApiOperation(value = "Gets booked rooms for the guest id and specific date. ",
            notes = "Gets booked rooms for the guest id and specific date. ")
    @RequestMapping(path = ROOMS_BY_GUEST_AND_DATE, method = GET, produces = APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = HttpStatus.FOUND, message = "Found the booked rooms. "),
            @ApiResponse(code = NOT_FOUND, message = "No booked rooms were found. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    @ResponseStatus(FOUND)
    public List<RoomByHotelAndDate> bookedRoomsByGuest(
            @ApiParam(required = true, value = "The UUID representation of the guest id. ")
            @PathVariable("guestId") @Valid UUID guestId,
            @PathVariable("date")
            @ApiParam(required = true, value = "Specific date to look at the booked rooms. ")
            @DateTimeFormat(pattern = "yyyy-MM-dd") @Valid LocalDate dateToLookFor) {
        LOGGER.info("Started looking for free rooms for the guest id '{}' and date '{}'", guestId, format(dateToLookFor));
        return guestService.findBookedRoomsForTheGuestIdAndDate(guestId, dateToLookFor);
    }
}
