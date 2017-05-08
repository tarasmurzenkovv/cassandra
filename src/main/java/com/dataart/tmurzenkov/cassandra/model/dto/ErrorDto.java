package com.dataart.tmurzenkov.cassandra.model;

import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;

/**
 * Dto to hold information about the exceptions/errors
 *
 * @author tmurzenkov
 */
public class ErrorDto {
    private String exceptionMessage;
    private String exceptionDescription;

    public ErrorDto(RecordExistsException e) {
        this.exceptionMessage = e.getMessage();
        this.exceptionDescription = "RECORD_ALREADY_EXISTS";
    }

    public ErrorDto() {
    }
}
