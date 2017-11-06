package com.dataart.tmurzenkov.cassandra.controller;


import com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus;
import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.service.BookingService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.BAD_REQUEST;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.CONFLICT;
import static com.dataart.tmurzenkov.cassandra.controller.uri.GuestUris.ADD_BOOKING;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * REST controller to manage the booking requests in the hotel reservation system.
 *
 * @author Taras_Murzenkov
 */
@RestController
@Api(value = "Booking operations", description = "REST API to manage booking requests in the reservation system. ")
public class BookingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuestController.class);
    @Autowired
    private BookingService bookingService;

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
}
