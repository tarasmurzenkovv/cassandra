package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.model.dto.ErrorDto;
import com.dataart.tmurzenkov.cassandra.model.exception.AlreadyBookedException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cassandra.support.exception.CassandraInvalidQueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor.Constants.*;

/**
 * Intercepts the application specific exceptions.
 *
 * @author tmurzenkov
 */
@ControllerAdvice
public class ExceptionInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInterceptor.class);

    public interface Constants {
        String RECORD_NOT_EXISTS = "RECORD_NOT_EXISTS";
        String RECORD_ALREADY_EXISTS = "RECORD_ALREADY_EXISTS";
        String ALREADY_BOOKED = "ALREADY_BOOKED";
        String QUERY_EXECUTION_EXCEPTION = "QUERY_EXECUTION_EXCEPTION";
        String UNKNOWN_EXCEPTION = "UNKNOWN_EXCEPTION";
    }

    /**
     * Logs {@link RecordExistsException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link RecordExistsException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.CONFLICT</code>
     */
    @ExceptionHandler(RecordExistsException.class)
    public ResponseEntity<ErrorDto> handle(RecordExistsException e) {
        return logAndMake(e, HttpStatus.CONFLICT, RECORD_ALREADY_EXISTS);
    }

    /**
     * Logs {@link RecordNotFoundException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link RecordNotFoundException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.NOT_FOUND</code>
     */
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorDto> handle(RecordNotFoundException e) {
        return logAndMake(e, HttpStatus.NOT_FOUND, RECORD_NOT_EXISTS);
    }

    /**
     * Logs {@link AlreadyBookedException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link AlreadyBookedException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.CONFLICT</code>
     */
    @ExceptionHandler(AlreadyBookedException.class)
    public ResponseEntity<ErrorDto> handle(AlreadyBookedException e) {
        return logAndMake(e, HttpStatus.CONFLICT, ALREADY_BOOKED);
    }

    /**
     * Logs {@link CassandraInvalidQueryException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link CassandraInvalidQueryException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.INTERNAL_SERVER_ERROR</code>
     */
    @ExceptionHandler(CassandraInvalidQueryException.class)
    public ResponseEntity<ErrorDto> handle(CassandraInvalidQueryException e) {
        return logAndMake(e, HttpStatus.INTERNAL_SERVER_ERROR, QUERY_EXECUTION_EXCEPTION);
    }

    /**
     * Logs {@link IllegalArgumentException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link IllegalArgumentException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.INTERNAL_SERVER_ERROR</code>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handle(IllegalArgumentException e) {
        return logAndMake(e, HttpStatus.BAD_REQUEST, QUERY_EXECUTION_EXCEPTION);
    }

    /**
     * Logs {@link RuntimeException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link RuntimeException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.INTERNAL_SERVER_ERROR</code>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handle(RuntimeException e) {
        return logAndMake(e, HttpStatus.INTERNAL_SERVER_ERROR, UNKNOWN_EXCEPTION);
    }

    private <T extends RuntimeException> ResponseEntity<ErrorDto> logAndMake(T e, HttpStatus httpStatus, String description) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e, description), httpStatus);
    }
}
