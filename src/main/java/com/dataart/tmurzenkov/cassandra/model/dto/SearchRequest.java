package com.dataart.tmurzenkov.cassandra.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.service.util.DateUtils.format;
import static java.lang.String.format;

/**
 * Search request to find free rooms for the hotel id, start and end dates.
 *
 * @author tmurzenkov
 */
@ApiModel(value = "SearchRequest", description = "The search request object.")
public class SearchRequest {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The starting date to look from. ", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    private final LocalDate start;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The ending date to look to. ", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    private final LocalDate end;
    @NotNull(message = "The hotel id must not be null. ")
    @ApiModelProperty(value = "The UUID representation of the hotel id", required = true, dataType = "String representation of the UUID. ")
    private final UUID hotelId;

    /**
     * Build search request. Throw the {@link IllegalArgumentException} if all input parameters are null or start date is
     * greater than end date.
     *
     * @param start   {@link LocalDate}
     * @param end     {@link LocalDate}
     * @param hotelId {@link UUID} hotel id
     */
    public SearchRequest(LocalDate start, LocalDate end, UUID hotelId) {
        if (!start.isBefore(end)) {
            final String message = format("Start date '%s' must go before end date '%s'", format(start), format(end));
            throw new IllegalArgumentException(message);
        }
        this.start = start;
        this.end = end;
        this.hotelId = hotelId;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public UUID getHotelId() {
        return hotelId;
    }

    @Override
    public String toString() {
        return "SearchRequest{"
                + "start=" + start
                + ", end=" + end
                + ", hotelId=" + hotelId
                + '}';
    }
}
