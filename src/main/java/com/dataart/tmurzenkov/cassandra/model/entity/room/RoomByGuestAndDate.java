package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;
import java.util.UUID;

import static org.springframework.cassandra.core.Ordering.DESCENDING;
import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * Stores the bookings.
 *
 * @author tmurzenkov
 */
@Table("room_booked_by_guest_and_date")
public class RoomByGuestAndDate {
    @PrimaryKeyColumn(name = "guest_id", type = PARTITIONED)
    private UUID guestId;
    @PrimaryKeyColumn(name = "booking_date", type = CLUSTERED, ordering = DESCENDING)
    private Date bookingDate;
    @Column("roomNumber")
    private Integer roomNumber;
    @Column("hotel_id")
    private UUID hotelId;
    @Column("confirmation_number")
    private String confirmationNumber;

    /**
     * Constructor.
     */
    public RoomByGuestAndDate() {
    }

    /**
     * Constructor.
     *
     * @param bookingRequest {@link BookingRequest}
     */
    public RoomByGuestAndDate(BookingRequest bookingRequest) {
        this.hotelId = bookingRequest.getHotelId();
        this.guestId = bookingRequest.getGuestId();
        this.bookingDate = bookingRequest.getEndDate();
        this.roomNumber = bookingRequest.getRoomNumber();
    }

    public UUID getHotelId() {
        return hotelId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }

    public UUID getGuestId() {
        return guestId;
    }

    public void setGuestId(UUID guestId) {
        this.guestId = guestId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }
}
