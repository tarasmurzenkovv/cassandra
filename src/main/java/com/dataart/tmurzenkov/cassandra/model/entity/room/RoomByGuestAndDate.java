package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table("room_booked_by_guest_and_date")
public class RoomByGuestAndDate extends BasicEntity {
    @PrimaryKeyColumn(name = "guest_id", type = PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "booking_date", type = PARTITIONED)
    private LocalDate bookingDate;
    @PrimaryKeyColumn(name = "room_number", type = CLUSTERED, ordering = DESCENDING)
    private Integer roomNumber;
    @Column("hotel_id")
    private UUID hotelId;
    @Column("confirmation_number")
    private String confirmationNumber;

    /**
     * Constructor.
     *
     * @param id          guest id
     * @param bookingDate reservation date
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
        this.hotelId = bookingRequest.getHotelId();
        this.bookingDate = bookingRequest.getBookingDate();
        this.roomNumber = bookingRequest.getRoomNumber();
    }

    @Override
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id)
                .with("bookingDate", this.bookingDate)
                .with("roomNumber", this.roomNumber).with("hotelId", this.hotelId);
    }
}
