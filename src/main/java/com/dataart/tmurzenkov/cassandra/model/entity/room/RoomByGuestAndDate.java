package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.time.LocalDate;
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
public class RoomByGuestAndDate extends BasicEntity {
    @PrimaryKeyColumn(name = "guest_id", type = PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "booking_date", type = CLUSTERED, ordering = DESCENDING)
    private LocalDate bookingDate;
    @Column("room_number")
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
     * @param id          guest id
     * @param bookingDate booking date
     * @param roomNumber  room number
     */
    public RoomByGuestAndDate(UUID id, LocalDate bookingDate, Integer roomNumber) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.roomNumber = roomNumber;
    }

    /**
     * Constructor.
     *
     * @param bookingRequest {@link BookingRequest}
     */
    public RoomByGuestAndDate(BookingRequest bookingRequest) {
        this.hotelId = bookingRequest.getHotelId();
        this.id = bookingRequest.getGuestId();
        this.bookingDate = bookingRequest.getBookingDate();
        this.roomNumber = bookingRequest.getRoomNumber();
    }

    @Override
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id).with("bookingDate", this.bookingDate);
    }

    public UUID getHotelId() {
        return hotelId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    @Override
    public String toString() {
        return "RoomByGuestAndDate{"
                + "id=" + id
                + ", bookingDate=" + bookingDate
                + ", roomNumber=" + roomNumber
                + ", hotelId=" + hotelId
                + ", confirmationNumber=" + confirmationNumber
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

        RoomByGuestAndDate that = (RoomByGuestAndDate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (bookingDate != null ? !bookingDate.equals(that.bookingDate) : that.bookingDate != null) {
            return false;
        }
        if (roomNumber != null ? !roomNumber.equals(that.roomNumber) : that.roomNumber != null) {
            return false;
        }
        if (hotelId != null ? !hotelId.equals(that.hotelId) : that.hotelId != null) {
            return false;
        }
        return confirmationNumber != null ? confirmationNumber.equals(that.confirmationNumber) : that.confirmationNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (bookingDate != null ? bookingDate.hashCode() : 0);
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        result = 31 * result + (hotelId != null ? hotelId.hashCode() : 0);
        result = 31 * result + (confirmationNumber != null ? confirmationNumber.hashCode() : 0);
        return result;
    }
}
