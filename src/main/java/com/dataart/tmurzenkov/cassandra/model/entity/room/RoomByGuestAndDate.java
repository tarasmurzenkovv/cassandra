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
    private Integer confirmationNumber;

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

    public Integer getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(Integer confirmationNumber) {
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
}
