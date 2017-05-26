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
    @PrimaryKeyColumn(name = "booking_date", type = CLUSTERED)
    private LocalDate bookingDate;
    @PrimaryKeyColumn(name = "status", type = CLUSTERED)
    private BookingStatus status;
    @Column(value = "room_number")
    private Integer roomNumber;

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
        return BasicMapId.id("id", this.id).with("bookingDate", this.bookingDate);
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
}
