package com.dataart.tmurzenkov.cassandra;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.dto.ErrorDto;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashSet;
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
        public static ResponseEntity<ErrorDto> build(RuntimeException e, String description, HttpStatus httpStatus) {
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
        public static ResponseEntity<ErrorDto> build(MethodArgumentNotValidException e, String description, HttpStatus httpStatus) {
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
     * Makes list from iterable.
     *
     * @param iterable {@link Iterable}
     * @param <T>      type of list
     * @return {@link List}
     */
    public static <T> List<T> makeList(Iterable<T> iterable) {
        List<T> objects = new ArrayList<>();
        iterable.iterator().forEachRemaining(objects::add);
        return objects;
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
         * Factory method to create empty hotel.
         *
         * @param hotelId {@link UUID}
         * @return {@link Hotel}
         */
        public static Hotel buildEmptyHotel(UUID hotelId) {
            final Hotel hotel = new Hotel();
            hotel.setId(hotelId);
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
         * Factory method to create search request.
         *
         * @param hotelId   {@link UUID}
         * @param daysToAdd {@link Integer}
         * @return {@link SearchRequest}
         */
        public static SearchRequest buildSearchRequest(Integer daysToAdd, UUID hotelId) {
            return new SearchRequest(now(), now().plusDays(daysToAdd), hotelId);
        }

        /**
         * Factory method to create reservation request.
         *
         * @return {@link BookingRequest}
         */
        public static BookingRequest buildBookingRequest() {
            return new BookingRequest(randomUUID(), randomUUID(), 2, now().plusDays(4));
        }

        /**
         * Factory method to create reservation request.
         *
         * @param guestId {@link UUID}
         * @param hotelId {@link UUID}
         * @return {@link BookingRequest}
         */
        public static BookingRequest buildBookingRequest(UUID hotelId, UUID guestId) {
            return new BookingRequest(guestId, hotelId, 2, now().plusDays(4));
        }

        /**
         * Factory method to create booking request.
         *
         * @param guestId    {@link UUID}
         * @param hotelId    {@link UUID}
         * @param roomNumber {@link Integer}
         * @return {@link BookingRequest}
         */
        public static BookingRequest buildBookingRequest(UUID hotelId, UUID guestId, Integer roomNumber) {
            return new BookingRequest(guestId, hotelId, roomNumber, now().plusDays(4));
        }

        /**
         * Factory method to create reservation request.
         *
         * @param guestId    {@link UUID}
         * @param hotelId    {@link UUID}
         * @param roomNumber {@link Integer}
         * @param localDate  {@link LocalDate}
         * @return {@link BookingRequest}
         */
        public static BookingRequest buildBookingRequest(UUID hotelId, UUID guestId, Integer roomNumber, LocalDate localDate) {
            return new BookingRequest(guestId, hotelId, roomNumber, localDate);
        }

        /**
         * Factory method to create list of rooms.
         *
         * @param hotelId    {@link UUID}
         * @param localDate  {@link LocalDate}
         * @return {@link BookingRequest}
         */
        public static List<RoomByHotelAndDate> buildRoomsInHotelAndDate(UUID hotelId, LocalDate localDate) {
            List<RoomByHotelAndDate> allRoomsInHotel = new ArrayList<>();

            RoomByHotelAndDate first = new RoomByHotelAndDate(hotelId, 1, localDate);
            RoomByHotelAndDate second = new RoomByHotelAndDate(hotelId, 2, localDate);
            RoomByHotelAndDate third = new RoomByHotelAndDate(hotelId, 2, localDate);
            third.setBookingStatus(BookingStatus.BOOKED);
            allRoomsInHotel.add(first);
            allRoomsInHotel.add(second);
            allRoomsInHotel.add(third);
            return allRoomsInHotel;
        }
    }

    /**
     * {@link RoomByHotelAndDate} test utils.
     *
     * @author tmurzenkov
     */
    public static class RoomTestUtils {
        /**
         * Factory method to create room.
         *
         * @return {@link RoomByHotelAndDate}
         */
        public static RoomByHotelAndDate buildRoom() {
            int randomRoomNumber = ThreadLocalRandom.current().nextInt(0, 11);
            return new RoomByHotelAndDate(UUID.randomUUID(), randomRoomNumber, LocalDate.now());
        }

        /**
         * Factory method to create room from hotel id.
         *
         * @param hotelId {@link UUID}
         * @return {@link RoomByHotelAndDate}
         */
        public static RoomByHotelAndDate buildRoom(UUID hotelId) {
            int randomRoomNumber = ThreadLocalRandom.current().nextInt(0, 11);
            return new RoomByHotelAndDate(hotelId, randomRoomNumber, LocalDate.now());
        }

        /**
         * Factory method to create room from hotel id.
         *
         * @param hotelId    {@link UUID}
         * @param roomNumber {@link Integer}
         * @param localDate  {@link LocalDate}
         * @return {@link RoomByHotelAndDate}
         */
        public static RoomByHotelAndDate buildRoom(UUID hotelId, Integer roomNumber, LocalDate localDate) {
            return new RoomByHotelAndDate(hotelId, roomNumber, localDate);
        }

        /**
         * Factory method to create list of rooms.
         *
         * @param numberToBuild {@link Integer}
         * @return list of {@link RoomByHotelAndDate}
         */
        public static List<RoomByHotelAndDate> buildRooms(Integer numberToBuild) {
            return IntStream.of(0, numberToBuild).mapToObj(ids -> buildRoom()).collect(toList());
        }

        /**
         * Factory method to create list of rooms from hotel id.
         *
         * @param numberToBuild {@link Integer}
         * @param hotelId       {@link UUID}
         * @return list of {@link RoomByHotelAndDate}
         */
        public static List<RoomByHotelAndDate> buildRooms(Integer numberToBuild, UUID hotelId) {
            return IntStream.range(1, numberToBuild + 1).mapToObj(ids -> buildRoom(hotelId, ids, LocalDate.now())).collect(toList());
        }

        /**
         * Factory method to create list of rooms from hotel id.
         *
         * @param numberToBuild {@link Integer}
         * @param hotelId       {@link UUID}
         * @param localDate     {@link LocalDate}
         * @return list of {@link RoomByHotelAndDate}
         */
        public static List<RoomByHotelAndDate> buildRooms(Integer numberToBuild, UUID hotelId, LocalDate localDate) {
            return IntStream.range(1, numberToBuild + 1).mapToObj(ids -> buildRoom(hotelId, ids, localDate)).collect(toList());
        }
    }
}


