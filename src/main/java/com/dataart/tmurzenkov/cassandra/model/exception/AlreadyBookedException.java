package com.dataart.tmurzenkov.cassandra.model.exception;

/**
 * Exception that will be thrown when the room is already booked.
 *
 * @author tmurzenkov
 */
public class AlreadyBookedException extends RuntimeException {
    /**
     * Constructs new exception from the given message.
     *
     * @param message {@link String}
     * @see RuntimeException
     */
    public AlreadyBookedException(String message) {
        super(message);
    }
}
