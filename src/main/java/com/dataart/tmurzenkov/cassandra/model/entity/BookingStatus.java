package com.dataart.tmurzenkov.cassandra.model.entity;

/**
 * Enum to represent the reservation status of the room.
 *
 * @author tmurzenkov
 */
public enum BookingStatus {
    BOOKED("booked"), FREE("free");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }
}
