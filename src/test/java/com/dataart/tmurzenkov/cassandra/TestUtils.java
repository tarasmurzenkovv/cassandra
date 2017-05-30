package com.dataart.tmurzenkov.cassandra;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.dto.ErrorDto;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

/**
 * Test Utils.
 *
 * @author tmurzenkov
 */
public final class TestUtils {
    private TestUtils() {
    }

    /**
     * Http response test utils.
     *
     * @author tmurzenkov
     */
    public static class HttpResponseTest {
        /**
         * Build response entity.
         *
         * @param e           {@link RuntimeException}
         * @param description {@link String}
         * @param httpStatus  {@link HttpStatus}
         * @return {@link ResponseEntity}
         */
        public static ResponseEntity build(RuntimeException e, String description, HttpStatus httpStatus) {
            return new ResponseEntity<>(new ErrorDto(e, description), httpStatus);
        }

        /**
         * Build response entity.
         *
         * @param e           {@link MethodArgumentNotValidException}
         * @param description {@link String}
         * @param httpStatus  {@link HttpStatus}
         * @return {@link ResponseEntity}
         */
        public static ResponseEntity build(MethodArgumentNotValidException e, String description, HttpStatus httpStatus) {
            return new ResponseEntity<>(new ErrorDto(e, description), httpStatus);
        }
    }

    /**
     * Converts entity to json string.
     *
     * @param entity {@link T} any type
     * @param <T>    any type
     * @return {@link String} json string
     * @throws JsonProcessingException could not write to string json
     */
    public static <T> String asJson(T entity) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(entity);
    }

    /**
     * Converts list of entities to json string.
     *
     * @param entities {@link List} of any type
     * @param <T>      any type
     * @return {@link String} json string
     * @throws JsonProcessingException could not write to string json
     */
    public static <T> String asJson(List<T> entities) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(entities);
    }

    /**
     * {@link Hotel} test utils.
     *
     * @author tmurzenkov
     */
    public static class HotelTestUtils {

        /**
         * Factory method to create hotel.
         *
         * @param hotelId {@link UUID}
         * @return {@link Hotel}
         */
        public static Hotel buildHotel(UUID hotelId) {
            final Hotel expectedHotelToAdd = new Hotel();
            expectedHotelToAdd.setAddress(buildAddress());
            expectedHotelToAdd.setId(hotelId);
            expectedHotelToAdd.setPhone("some phone");
            expectedHotelToAdd.setName("Hotel name");
            return expectedHotelToAdd;
        }

        /**
         * Factory method to create empty hotel.
         *
         * @return {@link Hotel}
         */
        public static Hotel buildEmptyHotel() {
            final Hotel hotel = new Hotel();
            hotel.setId(null);
            hotel.setAddress(null);
            hotel.setName(null);
            hotel.setPhone(null);
            return hotel;
        }

        /**
         * Factory method to create list of hotels.
         *
         * @return {@link List}
         */
        public static List<Hotel> buildHotels() {
            return IntStream.of(0, 3).mapToObj(id -> buildHotel(randomUUID())).collect(toList());
        }

        /**
         * Factory method to create hotel.
         *
         * @param hotelId      {@link UUID}
         * @param hotelAddress {@link Address}
         * @return {@link Hotel}
         */
        public static Hotel buildHotel(UUID hotelId, Address hotelAddress) {
            final Hotel expectedHotelToAdd = new Hotel();
            expectedHotelToAdd.setAddress(hotelAddress);
            expectedHotelToAdd.setId(hotelId);
            expectedHotelToAdd.setName("Hotel name");
            expectedHotelToAdd.setPhone("+3-8-096-166-66-66");
            return expectedHotelToAdd;
        }

        /**
         * Factory method to create {@link Address}.
         *
         * @return {@link Address}
         */
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

    /**
     * {@link Guest} test utils.
     *
     * @author tmurzenkov
     */
    public static class GuestTestUtils {
        /**
         * Factory method to create guest.
         *
         * @param guestId {@link UUID}
         * @return {@link Guest}
         */
        public static Guest buildNewGuest(UUID guestId) {
            final Guest guest = new Guest();
            guest.setId(guestId);
            guest.setFirstName("First name");
            guest.setLastName("Last name");
            guest.setEmails(new HashSet<>(asList("first@email.com", "second@email.com")));
            guest.setPhoneNumbers(new HashSet<>(asList("+3-8-099-999-99-99", "+3-8-011-111-11-11")));
            return guest;
        }

        /**
         * Factory method to create search request.
         *
         * @return {@link SearchRequest}
         */
        public static SearchRequest buildSearchRequest() {
            return new SearchRequest(now(), now().plusDays(2), randomUUID());
        }

        /**
         * Factory method to create booking request.
         *
         * @return {@link BookingRequest}
         */
        public static BookingRequest buildBookingRequest() {
            return new BookingRequest(randomUUID(), randomUUID(), 2, now().plusDays(4));
        }
    }

    /**
     * {@link Room} test utils.
     *
     * @author tmurzenkov
     */
    public static class RoomTestUtils {
        /**
         * Factory method to create room.
         *
         * @return {@link com.dataart.tmurzenkov.cassandra.model.entity.room.Room}
         */
        public static Room buildRoom() {
            int randomRoomNumber = ThreadLocalRandom.current().nextInt(0, 11);
            return new Room(UUID.randomUUID(), randomRoomNumber);
        }

        /**
         * Factory method to create list of rooms.
         *
         * @param numberToBuild {@link Integer}
         * @return list of {@link com.dataart.tmurzenkov.cassandra.model.entity.room.Room}
         */
        public static List<Room> buildRooms(Integer numberToBuild) {
            return IntStream.of(0, numberToBuild).mapToObj(ids -> buildRoom()).collect(toList());
        }
    }
}


