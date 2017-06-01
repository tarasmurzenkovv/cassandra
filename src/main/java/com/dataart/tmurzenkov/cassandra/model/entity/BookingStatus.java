package com.dataart.tmurzenkov.cassandra.model.entity;

/**
 * Enum to represent the reservation status of the room.
 * <p>
 * Booked -- payment already succeeded, reserved -- payment has not succeded but the confirmation number has been generated.
 *
 * @author tmurzenkov
 */
public enum BookingStatus {
    BOOKED("booked"), RESERVED("reserved");

    private final String status;

    BookingStatus(String status) {
        this.status = status;
    }
}
