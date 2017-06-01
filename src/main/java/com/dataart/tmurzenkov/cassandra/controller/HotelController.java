package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.ADD_HOTEL;
import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.HOTELS_IN_THE_CITY;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.CREATED;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.FOUND;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.NOT_FOUND;
import static com.dataart.tmurzenkov.cassandra.controller.status.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Hotel REST controller.
 *
 * @author tmurzenkov
 */
@RestController
@Api(description = "REST API to manage hotels in the reservation system. ")
public class HotelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelServiceImpl;
    private final ServiceResourceAssembler<Hotel, Class<HotelController>> resourceAssembler;

    /**
     * Autowire the below services into the controller.
     *
     * @param resourceAssembler {@link ServiceResourceAssembler}
     * @param hotelServiceImpl  {@link HotelService}
     */
    public HotelController(HotelService hotelServiceImpl, ServiceResourceAssembler<Hotel, Class<HotelController>> resourceAssembler) {
        this.hotelServiceImpl = hotelServiceImpl;
        this.resourceAssembler = resourceAssembler;
    }

    /**
     * Gets all hotels in the city.
     *
     * @param city city name
     * @return list of the {@link Hotel}
     */
    @ApiOperation(value = "Finds all hotels in the city.", notes = "Finds all hotels in the city. ")
    @RequestMapping(path = HOTELS_IN_THE_CITY, method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.FOUND)
    @ApiResponses({
            @ApiResponse(code = FOUND, message = "Found all hotels for the city name. "),
            @ApiResponse(code = NOT_FOUND, message = "Not found all hotels for the city name. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    public List<Resource<Hotel>> getAllHotelsInTheCity(@ApiParam(value = "Name of the city", required = true)
                                                       @PathVariable("city") String city) {
        LOGGER.info("Going to look for all hotels in the city '{}'", city);
        List<Hotel> allHotelsInTheCity = hotelServiceImpl.findAllHotelsInTheCity(city);
        return resourceAssembler.toResource(allHotelsInTheCity);
    }

    /**
     * Adds new hotel to the system.
     *
     * @param hotel new hotel
     * @return {@link ResponseEntity} with the location header.
     */
    @ApiOperation(value = "Adds new hotel to the system.", notes = "Adds new hotel to the system and returns the location header. ")
    @RequestMapping(path = ADD_HOTEL, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = CREATED, message = "Successfully added hotel to the system. "),
            @ApiResponse(code = BAD_REQUEST, message = "Invalid type of the parameters. ")})
    public Resource<Hotel> addHotel(
            @ApiParam(value = "Hotel dto. ", required = true)
            @RequestBody Hotel hotel) {
        LOGGER.info("Going to add the following hotel to the system '{}'", hotel);
        Hotel addedHotel = hotelServiceImpl.addHotel(hotel);
        return resourceAssembler.withController(HotelController.class).toResource(addedHotel);
    }
}
