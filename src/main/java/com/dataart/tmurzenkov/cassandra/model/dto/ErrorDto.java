package com.dataart.tmurzenkov.cassandra.model.dto;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static java.util.stream.Collectors.joining;

/**
 * Dto to hold information about the exceptions/errors.
 *
 * @author tmurzenkov
 */
public class ErrorDto {
    private String exceptionMessage;
    private String exceptionDescription;

    /**
     * Constructs DTO from {@link MethodArgumentNotValidException}.
     *
     * @param e {@link MethodArgumentNotValidException}
     * @param exceptionDescription {@link String} short camel case exception description.
     */
    public ErrorDto(MethodArgumentNotValidException e, String exceptionDescription) {
        this.exceptionMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(joining("; "));
        this.exceptionDescription = exceptionDescription;
    }

    /**
     * Constructs DTO from {@link RuntimeException}.
     *
     * @param e {@link RuntimeException}
     * @param exceptionDescription {@link String} short camel case exception description.
     */
    public ErrorDto(RuntimeException e, String exceptionDescription) {
        this.exceptionMessage = e.getMessage();
        this.exceptionDescription = exceptionDescription;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionDescription() {
        return exceptionDescription;
    }

    public void setExceptionDescription(String exceptionDescription) {
        this.exceptionDescription = exceptionDescription;
    }
}
