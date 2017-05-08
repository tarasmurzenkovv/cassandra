package com.dataart.tmurzenkov.cassandra.service.impl.advise;

import com.dataart.tmurzenkov.cassandra.model.dto.ErrorDto;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cassandra.support.exception.CassandraInvalidQueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Intercepts the application specific exceptions.
 *
 * @author tmurzenkov
 */
@ControllerAdvice
public class ExceptionInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInterceptor.class);

    /**
     * Logs {@link RecordExistsException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link RecordExistsException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.CONFLICT</code>
     */
    @ExceptionHandler(RecordExistsException.class)
    public ResponseEntity<ErrorDto> handle(RecordExistsException e) {
        return logAndMake(e, HttpStatus.CONFLICT, "RECORD_ALREADY_EXISTS");
    }

    /**
     * Logs {@link CassandraInvalidQueryException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link CassandraInvalidQueryException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.INTERNAL_SERVER_ERROR</code>
     */
    @ExceptionHandler(CassandraInvalidQueryException.class)
    public ResponseEntity<ErrorDto> handle(CassandraInvalidQueryException e) {
        return logAndMake(e, HttpStatus.INTERNAL_SERVER_ERROR, "QUERY_EXECUTION_EXCEPTION");
    }

    /**
     * Logs {@link RuntimeException} and transforms to {@link ResponseEntity} with {@link ErrorDto}.
     *
     * @param e {@link RuntimeException}
     * @return {@link ResponseEntity} with status <code>HttpStatus.INTERNAL_SERVER_ERROR</code>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handle(RuntimeException e) {
        return logAndMake(e, HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_EXCEPTION");
    }

    private <T extends RuntimeException> ResponseEntity<ErrorDto> logAndMake(T e, HttpStatus httpStatus, String description) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e, description), httpStatus);
    }
}
