package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.model.entity.Hotel;
import com.dataart.tmurzenkov.cassandra.service.HotelService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Hotel REST controller.
 * "api" / "get" / "city"
 * "api" / "add" / "hotel"
 *
 * @author tmurzenkov
 */
@RestController
@Api(description = "REST API to manage hotels in the booking system. ")
public class HotelController {

    @Autowired
    private HotelService hotelServiceImpl;
    @Autowired
    private ResourceAssembler<Hotel, Resource<Hotel>> resourceAssembler;

    /**
     * Gets all hotels in the city.
     *
     * @param city city name
     * @return list of the {@link Hotel}
     */
    @ApiOperation(value = "Finds all hotels in the city.", notes = "Finds all hotels in the city. ")
    @RequestMapping(path = "/api/get/{city}")
    public List<Resource<Hotel>> getAllHotelsInTheCity(
            @ApiParam(value = "Name of the city to where to search", required = true)
            @PathVariable("city") String city) {
        return hotelServiceImpl.findAllHotelsInTheCity(city)
                .stream()
                .map(hotel -> resourceAssembler.toResource(hotel))
                .collect(toList());
    }

    /**
     * Adds new hotel to the system.
     *
     * @param hotel new hotel
     * @return {@link ResponseEntity} with the location header.
     */
    @ApiOperation(value = "Adds new hotel to the system.", notes = "Adds new hotel to the system and returns the location header. ")
    @RequestMapping(path = "/api/add/hotel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource<Hotel> addHotel(
            @ApiParam(value = "Hotel dto. ", required = true)
            @RequestBody Hotel hotel) {
        Hotel saved = hotelServiceImpl.addNewHotelToTheSystem(hotel);
        return resourceAssembler.toResource(saved);
    }
}
