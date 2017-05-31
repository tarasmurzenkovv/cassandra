package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * Maps to the table room_booked_by_hotel_and_date.
 *
 * @author tmurzenkov
 */
@Table("room_by_hotel_and_date_and_status")
public class RoomByHotelAndDate extends BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "room_number", type = PARTITIONED)
    private Integer roomNumber;
    @PrimaryKeyColumn(name = "booking_date", type = CLUSTERED)
    private LocalDate bookingDate;
    @Column
    private BookingStatus status;

    /**
     * Default constructor.
     */
    public RoomByHotelAndDate() {
    }

    /**
     * Constructs from {@link BookingRequest} and {@link BookingStatus}.
     *
     * @param bookingRequest {@link BookingRequest}
     * @param bookingStatus  {@link BookingStatus}
     */
    public RoomByHotelAndDate(BookingRequest bookingRequest, BookingStatus bookingStatus) {
        this.id = bookingRequest.getHotelId();
        this.bookingDate = bookingRequest.getBookingDate();
        this.status = bookingStatus;
        this.roomNumber = bookingRequest.getRoomNumber();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id).with("roomNumber", this.roomNumber);
    }

    @Override
    public String toString() {
        return "RoomByHotelAndDate{"
                + "id=" + id
                + ", bookingDate=" + bookingDate
                + ", status=" + status
                + ", roomNumber=" + roomNumber
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

        RoomByHotelAndDate that = (RoomByHotelAndDate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (bookingDate != null ? !bookingDate.equals(that.bookingDate) : that.bookingDate != null) {
            return false;
        }
        if (status != that.status) {
            return false;
        }
        return roomNumber != null ? roomNumber.equals(that.roomNumber) : that.roomNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (bookingDate != null ? bookingDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        return result;
    }
}
