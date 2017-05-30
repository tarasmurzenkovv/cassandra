package com.dataart.tmurzenkov.cassandra.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the booking request dto object. JSR 303 annotations are being used to validate the dto.
 *
 * @author tmurzenkov
 */
@ApiModel(value = "BookingRequest", description = "The booking request object.")
public class BookingRequest {
    @NotNull(message = "The guest id must not be null. ")
    @ApiModelProperty(value = "The UUID representation of the guest id", required = true, dataType = "String representation of the UUID. ")
    private UUID guestId;
    @NotNull(message = "The hotel id must not be null. ")
    @ApiModelProperty(value = "The UUID representation of the hotel id", required = true, dataType = "String representation of the UUID. ")
    private UUID hotelId;
    @NotNull(message = "The room number must not be null. ")
    @Min(message = "The room number must be more than zero. ", value = 0)
    @ApiModelProperty(value = "The room number", required = true, dataType = "String representation of the integer. ")
    private Integer roomNumber;
    @NotNull(message = "The booking date must not be null. ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The booking date", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate bookingDate;

    /**
     * Constructor.
     */
    public BookingRequest() {
    }

    /**
     * Constructor.
     *
     * @param guestId     {@link UUID}
     * @param hotelId     {@link UUID}
     * @param roomNumber  {@link Integer}
     * @param bookingDate {@link LocalDate}
     */
    public BookingRequest(UUID guestId, UUID hotelId, Integer roomNumber, LocalDate bookingDate) {
        this.guestId = guestId;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.bookingDate = bookingDate;
    }

    public UUID getGuestId() {
        return guestId;
    }

    public void setGuestId(UUID guestId) {
        this.guestId = guestId;
    }

    public UUID getHotelId() {
        return hotelId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return "BookingRequest{"
                + "guestId=" + guestId
                + ", hotelId=" + hotelId
                + ", roomNumber=" + roomNumber
                + ", bookingDate=" + bookingDate
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookingRequest that = (BookingRequest) o;

        if (guestId != null ? !guestId.equals(that.guestId) : that.guestId != null) {
            return false;
        }
        if (hotelId != null ? !hotelId.equals(that.hotelId) : that.hotelId != null) {
            return false;
        }
        if (roomNumber != null ? !roomNumber.equals(that.roomNumber) : that.roomNumber != null) {
            return false;
        }
        return bookingDate != null ? bookingDate.equals(that.bookingDate) : that.bookingDate == null;
    }

    @Override
    public int hashCode() {
        int result = guestId != null ? guestId.hashCode() : 0;
        result = 31 * result + (hotelId != null ? hotelId.hashCode() : 0);
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        result = 31 * result + (bookingDate != null ? bookingDate.hashCode() : 0);
        return result;
    }
}
