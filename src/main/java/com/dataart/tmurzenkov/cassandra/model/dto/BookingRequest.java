package com.dataart.tmurzenkov.cassandra.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Represents the booking request dto object. JSR 303 annotations are being used to validate the dto.
 *
 * @author tmurzenkov
 */
@ApiModel(value = "BookingRequest", description = "The booking request object.")
public class BookingRequest {
    @NotNull
    @ApiModelProperty(value = "The UUID representation of the guest id", required = true, dataType = "String representation of the UUID. ")
    private UUID guestId;
    @NotNull
    @ApiModelProperty(value = "The UUID representation of the hotel id", required = true, dataType = "String representation of the UUID. ")
    private UUID hotelId;
    @NotNull
    @ApiModelProperty(value = "The room number", required = true, dataType = "String representation of the integer. ")
    private Integer roomNumber;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The starting date for the booking time range", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    private Date startDate;
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "The ending date for the booking time range", required = true, dataType = "Date in format 'yyyy-MM-dd'. ")
    private Date endDate;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "BookingRequest{"
                + "guestId=" + guestId
                + ", hotelId=" + hotelId
                + ", roomNumber=" + roomNumber
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + '}';
    }
}
