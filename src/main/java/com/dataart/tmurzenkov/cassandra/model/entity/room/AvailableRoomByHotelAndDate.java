package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.dto.BookingRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;

/**
 * AvailableRoomByHotelAndDate cassandra entity.
 *
 * @author tmurzenkov
 */

@Table("available_rooms_by_hotel_date")
public class AvailableRoomByHotelAndDate extends BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "date", type = CLUSTERED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    @PrimaryKeyColumn(name = "room_number", type = CLUSTERED)
    private Integer roomNumber;
    @Column("is_available")
    private Boolean isAvailable;

    /**
     * Constructor.
     */
    public AvailableRoomByHotelAndDate() {
    }


    /**
     * Constructs from the provided hotel id and room number and booking date.
     *
     * @param id         {@link UUID}
     * @param roomNumber {@link Integer}
     * @param localDate  {@link LocalDate}
     */
    public AvailableRoomByHotelAndDate(UUID id, Integer roomNumber, LocalDate localDate) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.date = localDate;
        this.setAvailable(true);
    }

    /**
     * Builds from {@link BookingRequest}.
     *
     * @param bookingRequest {@link BookingRequest}
     */
    public AvailableRoomByHotelAndDate(BookingRequest bookingRequest) {
        this.id = bookingRequest.getHotelId();
        this.roomNumber = bookingRequest.getRoomNumber();
        this.date = bookingRequest.getBookingDate();
        this.setAvailable(false);
    }

    /**
     * Constructs from {@link RoomByGuestAndDate}.
     *
     * @param roomByGuestAndDate {@link RoomByGuestAndDate}
     */
    public AvailableRoomByHotelAndDate(RoomByGuestAndDate roomByGuestAndDate) {
        this(roomByGuestAndDate.getHotelId(), roomByGuestAndDate.getRoomNumber(), roomByGuestAndDate.getBookingDate());
    }

    /**
     * Constructs from {@link RoomByHotelAndDate}.
     *
     * @param roomByHotelAndDate {@link RoomByHotelAndDate}
     */
    public AvailableRoomByHotelAndDate(RoomByHotelAndDate roomByHotelAndDate) {
        this(roomByHotelAndDate.getId(), roomByHotelAndDate.getRoomNumber(), roomByHotelAndDate.getBookingDate());
    }

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id)
                .with("date", this.date)
                .with("roomNumber", this.roomNumber);
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(final Boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AvailableRoomByHotelAndDate availableRoomByHotelAndDate = (AvailableRoomByHotelAndDate) o;

        if (id != null ? !id.equals(availableRoomByHotelAndDate.id) : availableRoomByHotelAndDate.id != null) {
            return false;
        }
        return roomNumber != null
                ? roomNumber.equals(availableRoomByHotelAndDate.roomNumber) : availableRoomByHotelAndDate.roomNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AvailableRoomByHotelAndDate{"
                + "hotel_id=" + id
                + ", roomNumber=" + roomNumber
                + '}';
    }
}
