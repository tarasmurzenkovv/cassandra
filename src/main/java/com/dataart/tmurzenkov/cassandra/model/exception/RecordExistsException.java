package com.dataart.tmurzenkov.cassandra.model.exception;


/**
 * Exception that will be thrown when the give record is in DB.
 *
 * @author tmurzenkov
 */
public class RecordExistsException extends RuntimeException {

    /**
     * Constructs new exception from the given message.
     *
     * @param message {@link String}
     * @see RuntimeException
     */
    public RecordExistsException(String message) {
        super(message);
    }
}
