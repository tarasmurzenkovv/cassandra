package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.service.GuestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Booking controller.
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
    @RequestMapping(path = "/api/add/guest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Guest> registerNewGuest(@RequestBody Guest guest) {
        guestService.registerNewGuest(guest);
        return resourceAssembler.toResource(guest);
    }
}
