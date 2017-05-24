package com.dataart.tmurzenkov.cassandra.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Represents the booking request dto object. JSR 303 annotations are being used to validate the dto.
 *
 * @author tmurzenkov
 */
public class BookingRequest {
    @NotNull
    private UUID guestId;
    @NotNull
    private UUID hotelId;
    @NotNull
    private Integer roomNumber;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
