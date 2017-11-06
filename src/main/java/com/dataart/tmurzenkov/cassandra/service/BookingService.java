package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;

/**
 * Service that is responsible to manage booking requests.
 *
 * @author Taras_Murzenkov
 */
public interface BookingService {
    /**
     * Registers the booking request into the database
     *
     * @param bookingRequest instance of {@link BookingRequest}.
     * @return registered instance of {@link BookingRequest}
     */
    BookingRequest performBooking(BookingRequest bookingRequest);

    /**
     * Assigns the unique indentifier for the particular instance o {@link BookingRequest}
     *
     * @param bookingRequest instance of {@link BookingRequest}
     * @return {@link Integer} the confirmation number for the specific {@link BookingRequest}
     */
    Integer generateConfirmationNumber(BookingRequest bookingRequest);
}
