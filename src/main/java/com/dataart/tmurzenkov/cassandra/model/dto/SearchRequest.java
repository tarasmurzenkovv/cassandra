package com.dataart.tmurzenkov.cassandra.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.util.DateUtils.format;
import static java.lang.String.format;

/**
 * Search request to find free rooms for the hotel id, start and end dates.
 *
 * @author tmurzenkov
 */
@ApiModel(value = "SearchRequest", description = "The search request object.")
public class SearchRequest {
    @NotNull(message = "The starting date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The starting date to look from. ", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate start;
    @NotNull(message = "The ending date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The ending date to look to. ", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate end;
    @NotNull(message = "The hotel id must not be null. ")
    @ApiModelProperty(value = "The UUID representation of the hotel id", required = true, dataType = "String representation of the UUID. ")
    private final UUID hotelId;

    /**
     * Default no arg constructor. All fields will be set to null.
     */
    public SearchRequest() {
        this.start = null;
        this.end = null;
        this.hotelId = null;
    }

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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SearchRequest that = (SearchRequest) o;

        if (start != null ? !start.equals(that.start) : that.start != null) {
            return false;
        }
        if (end != null ? !end.equals(that.end) : that.end != null) {
            return false;
        }
        return hotelId != null ? hotelId.equals(that.hotelId) : that.hotelId == null;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (hotelId != null ? hotelId.hashCode() : 0);
        return result;
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
