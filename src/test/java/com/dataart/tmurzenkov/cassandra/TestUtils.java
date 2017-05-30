package com.dataart.tmurzenkov.cassandra;

import com.dataart.tmurzenkov.cassandra.model.dto.ErrorDto;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Test Utils.
 *
 * @author tmurzenkov
 */
public final class TestUtils {
    private TestUtils() {
    }

    public static class HttpResponseTest {
        public static ResponseEntity build(RuntimeException e, String description, HttpStatus httpStatus) {
            return new ResponseEntity<>(new ErrorDto(e, description), httpStatus);
        }
    }

    public static <T> String asJson(T entity) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(entity);
    }

    public static <T> String asJson(List<T> entities) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(entities);
    }

    public static Hotel buildHotel(UUID hotelId) {
        final Hotel expectedHotelToAdd = new Hotel();
        expectedHotelToAdd.setAddress(buildAddress());
        expectedHotelToAdd.setId(hotelId);
        expectedHotelToAdd.setPhone("some phone");
        expectedHotelToAdd.setName("Hotel name");
        return expectedHotelToAdd;
    }

    public static Hotel buildEmptyHotel() {
        final Hotel hotel = new Hotel();
        hotel.setId(null);
        hotel.setAddress(null);
        hotel.setName(null);
        hotel.setPhone(null);
        return hotel;
    }

    public static List<Hotel> buildHotels() {
        return IntStream.of(0, 3).mapToObj(id -> buildHotel(UUID.randomUUID())).collect(toList());
    }

    public static Hotel buildHotel(UUID hotelId, Address hotelAddress) {
        final Hotel expectedHotelToAdd = new Hotel();
        expectedHotelToAdd.setAddress(hotelAddress);
        expectedHotelToAdd.setId(hotelId);
        expectedHotelToAdd.setName("Hotel name");
        expectedHotelToAdd.setPhone("+3-8-096-166-66-66");
        return expectedHotelToAdd;
    }

    public static Address buildAddress() {
        final Address hotelAddress = new Address();
        hotelAddress.setStreet("Hotel street");
        hotelAddress.setCountry("Hotel country");
        hotelAddress.setPostalCode("69104");
        hotelAddress.setStateOrProvince("Hotel province");
        hotelAddress.setCity("City name");
        return hotelAddress;
    }
}
