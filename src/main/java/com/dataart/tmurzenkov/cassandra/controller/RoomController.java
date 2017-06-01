package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.AvailableRoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.CREATED;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.FOUND;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.NOT_FOUND;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.BAD_REQUEST;
import static com.dataart.tmurzenkov.cassandra.controller.uri.RoomUris.GET_FREE_ROOMS;
import static com.dataart.tmurzenkov.cassandra.controller.uri.Uris.ADD_ROOM;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * AvailableRoomByHotelAndDate controller.
 *
 * @author tmurzenkov
 */
@RestController
@Api(value = "AvailableRoomByHotelAndDate operations. ", description = "REST API to manage hotel rooms in the reservation system. ")
public class RoomController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);
    private final ServiceResourceAssembler<AvailableRoomByHotelAndDate, Class<RoomController>> resourceAssembler;
    private final RoomService roomService;

    /**
     * Autowire the below services into the controller.
     *
     * @param resourceAssembler {@link ServiceResourceAssembler}
     * @param roomService       {@link RoomService}
     */
    public RoomController(ServiceResourceAssembler<AvailableRoomByHotelAndDate, Class<RoomController>> resourceAssembler,
                          RoomService roomService) {
        this.resourceAssembler = resourceAssembler;
        this.roomService = roomService;
    }

    /**
     * Adds new hotel availableRoomByHotelAndDate to the system.
     *
     * @param availableRoomByHotelAndDate {@link AvailableRoomByHotelAndDate}
     * @return {@link Resource}
     */
    @ApiOperation(value = "Adds new availableRoomByHotelAndDate to the system.",
            notes = "Adds new hotel availableRoomByHotelAndDate to the system and returns the location header. ")
    @RequestMapping(path = ADD_ROOM, method = POST, consumes = "application/json", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = CREATED, message = "Add availableRoomByHotelAndDate to the hotel. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    public Resource<AvailableRoomByHotelAndDate> addRoomToTheHotel(@RequestBody @Valid
                                                                           AvailableRoomByHotelAndDate availableRoomByHotelAndDate) {
        LOGGER.info("Going to add the following availableRoomByHotelAndDate into the data base '{}'", availableRoomByHotelAndDate);
        AvailableRoomByHotelAndDate addedAvailableRoomByHotelAndDate = roomService.addRoomToHotel(availableRoomByHotelAndDate);
        return resourceAssembler.withController(RoomController.class).toResource(addedAvailableRoomByHotelAndDate);
    }

    /**
     * Find free rooms by hotel id and within time interval.
     *
     * @param searchRequest {@link SearchRequest}
     * @return {@link List} of {@link Resource} of {@link AvailableRoomByHotelAndDate}
     */
    @ApiOperation(value = "Finds free rooms.", notes = "Finds free rooms by hotel id")
    @RequestMapping(path = GET_FREE_ROOMS, method = POST, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    @ApiResponses({
            @ApiResponse(code = FOUND, message = "Found free rooms in the hotel. "),
            @ApiResponse(code = NOT_FOUND, message = "Not found free rooms in the hotel. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    public List<Resource<AvailableRoomByHotelAndDate>> findFreeRooms(@RequestBody @Valid SearchRequest searchRequest) {
        LOGGER.info("Going to find the free rooms for the following request: '{}'", searchRequest);
        List<AvailableRoomByHotelAndDate> freeRoomsByHotelId = roomService.findFreeRoomsInTheHotel(searchRequest);
        return resourceAssembler.toResource(freeRoomsByHotelId);
    }
}
