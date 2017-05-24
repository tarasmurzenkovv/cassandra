package com.dataart.tmurzenkov.cassandra.model.exception;

/**
 * Exception that will be thrown when the give record is not in DB.
 *
 * @author tmurzenkov
 */
public class RecordNotFoundException extends RuntimeException {
    /**
     * Constructs new exception from the given message.
     *
     * @param message {@link String}
     * @see RuntimeException
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}
