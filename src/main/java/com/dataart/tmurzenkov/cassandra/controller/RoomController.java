package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Room controller.
 * "api"/"get"/"freerooms"
 *
 * @author tmurzenkov
 */
@RestController
@Api(description = "REST API to manage hotel rooms in the booking system. ")
public class RoomController {
    @Autowired
    private ResourceAssembler<Room, Resource<Room>> resourceAssembler;
    @Autowired
    private RoomService roomService;

    /**
     * Adds new hotel room to the system.
     *
     * @param room {@link Room}
     * @return {@link Resource}
     */
    @ApiOperation(value = "Adds new hotel room to the system.",
            notes = "Adds new hotel room to the system and returns the location header. ")
    @RequestMapping(path = "/api/add/room", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<Room> addRoomToTheHotel(@RequestBody Room room) {
        roomService.addRoomToHotel(room);
        return resourceAssembler.toResource(room);
    }

    /**
     * Find free rooms by hotel id.
     *
     * @param hotelId {@link UUID}
     * @param start   {@link Date} start date
     * @param end     {@link Date} end date
     * @return {@link List} of {@link Resource} of {@link Room}
     */
    @ApiOperation(value = "Finds free rooms.",
            notes = "Finds free rooms by hotel id")
    @RequestMapping(path = "/api/get/freerooms/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Resource<Room>> findFreeRooms(@RequestParam("hotelId") UUID hotelId,
                                              @RequestParam("start") Date start,
                                              @RequestParam("end") Date end) {
        List<Room> freeRoomsByHotelId = roomService.findFreeRoomsInTheHotel(start, end, hotelId);
        return freeRoomsByHotelId.stream().map(resourceAssembler::toResource).collect(toList());
    }

}
