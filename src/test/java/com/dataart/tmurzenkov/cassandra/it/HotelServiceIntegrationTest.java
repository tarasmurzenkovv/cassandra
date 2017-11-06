package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.controller.HotelController;
import com.dataart.tmurzenkov.cassandra.controller.uri.Uris;
import com.dataart.tmurzenkov.cassandra.dao.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.service.impl.service.HotelServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.ServiceResourceAssembler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;

import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildAddress;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildHotel;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildEmptyHotel;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HotelTestUtils.buildHotels;
import static com.dataart.tmurzenkov.cassandra.TestUtils.HttpResponseTest.build;
import static com.dataart.tmurzenkov.cassandra.TestUtils.asJson;
import static com.dataart.tmurzenkov.cassandra.TestUtils.makeList;
import static com.dataart.tmurzenkov.cassandra.controller.uri.HotelUris.ADD_HOTEL;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.QUERY_EXECUTION_EXCEPTION;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_ALREADY_EXISTS;
import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.RECORD_NOT_EXISTS;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link HotelServiceImpl}.
 *
 * @author tmurzenkov
 */
public class HotelServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private HotelByCityDao hotelByCityDao;
    @Autowired
    private ServiceResourceAssembler<Hotel, Class<HotelController>> resourceResourceAssembler;

    @Test
    public void shouldAddANewHotelToTheSystem() throws Exception {
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final Resource<Hotel> hotelResource = resourceResourceAssembler
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        assertTrue(hotelDao.exists(expectedHotelToAdd.getCompositeId()));
        final Hotel actualHotel = hotelDao.findOne(hotelId);
        assertEquals(expectedHotelToAdd, actualHotel);
    }

    @Test
    public void shouldNotAddATwiceTheSameHotelToTheSystem() throws Exception {
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final String message = format("Such hotel information is already added to the data base '%s'", expectedHotelToAdd);
        final RuntimeException exception = new IllegalArgumentException(message);
        final Resource<Hotel> hotelResource = resourceResourceAssembler
                .withController(HotelController.class)
                .toResource(expectedHotelToAdd);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJson(hotelResource)));

        assertTrue(hotelDao.exists(expectedHotelToAdd.getCompositeId()));
        final Hotel actualHotel = hotelDao.findOne(hotelId);
        assertEquals(expectedHotelToAdd, actualHotel);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(asJson(build(exception, RECORD_ALREADY_EXISTS, BAD_REQUEST).getBody())));

        List<Hotel> foundHotels = makeList(hotelDao.findAll());
        assertEquals(foundHotels.size(), 1);
        assertTrue(foundHotels.contains(expectedHotelToAdd));
    }

    @Test
    public void shouldNotAddEmptyHotelToTheSystem() throws Exception {
        final UUID hotelId = UUID.randomUUID();
        final Hotel expectedHotelToAdd = buildEmptyHotel(hotelId);
        final String message = "Cannot add the hotel with empty name. ";
        final RuntimeException exception = new IllegalArgumentException(message);

        mockMvc
                .perform(post(ADD_HOTEL).content(asJson(expectedHotelToAdd)).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(asJson(build(exception, QUERY_EXECUTION_EXCEPTION, BAD_REQUEST).getBody())));

        assertTrue(null == hotelDao.findOne(expectedHotelToAdd.getId()));
    }

    @Test
    public void shouldFindAllHotelsForTheCityName() throws Exception {
        final String cityName = "City name";
        final List<Hotel> expectedHotelList = buildHotels();
        final List<HotelByCity> expectedHotelsByCity = expectedHotelList.stream().map(HotelByCity::new).collect(toList());
        expectedHotelList.forEach(hotel -> {
            try {
                mockMvc.perform(post(ADD_HOTEL).content(asJson(hotel)).contentType(APPLICATION_JSON));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });

        mockMvc.perform(get(Uris.HOTELS_IN_THE_CITY, cityName)).andExpect(status().isFound());

        hotelDao.findAll().forEach(actualHotel -> assertTrue(expectedHotelList.contains(actualHotel)));
        hotelByCityDao.findAll().forEach(actualHotelByCity -> assertTrue(expectedHotelsByCity.contains(actualHotelByCity)));
    }

    @Test
    public void shouldNotFindHotelsForTheUnexistingCityName() throws Exception {
        final String cityName = "City name that do not exists";
        final String message = format("Cannot find hotels for the given city '%s'", cityName);
        final RuntimeException exception = new IllegalArgumentException(message);
        final List<Hotel> expectedHotelList = buildHotels();

        expectedHotelList.forEach(hotel -> {
            try {
                mockMvc.perform(post(ADD_HOTEL).content(asJson(hotel)).contentType(APPLICATION_JSON));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        mockMvc.perform(get(Uris.HOTELS_IN_THE_CITY, cityName))
                .andExpect(status().isNotFound())
                .andExpect(content().string(asJson(build(exception, RECORD_NOT_EXISTS, NOT_FOUND).getBody())));
    }
}
