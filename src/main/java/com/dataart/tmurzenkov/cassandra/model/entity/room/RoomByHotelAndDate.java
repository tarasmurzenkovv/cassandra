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
 * RoomByHotelAndDate cassandra entity.
 *
 * @author tmurzenkov
 */

@Table("available_rooms_by_hotel_date")
public class RoomByHotelAndDate extends BasicEntity {
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
    public RoomByHotelAndDate() {
    }


    /**
     * Constructs from the provided hotel id and room number and booking date.
     *
     * @param id         {@link UUID}
     * @param roomNumber {@link Integer}
     * @param localDate  {@link LocalDate}
     */
    public RoomByHotelAndDate(UUID id, Integer roomNumber, LocalDate localDate) {
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
    public RoomByHotelAndDate(BookingRequest bookingRequest) {
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
    public RoomByHotelAndDate(RoomByGuestAndDate roomByGuestAndDate) {
        this(roomByGuestAndDate.getHotelId(), roomByGuestAndDate.getRoomNumber(), roomByGuestAndDate.getBookingDate());
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

        RoomByHotelAndDate roomByHotelAndDate = (RoomByHotelAndDate) o;

        if (id != null ? !id.equals(roomByHotelAndDate.id) : roomByHotelAndDate.id != null) {
            return false;
        }
        return roomNumber != null
                ? roomNumber.equals(roomByHotelAndDate.roomNumber) : roomByHotelAndDate.roomNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RoomByHotelAndDate{"
                + "hotel_id=" + id
                + ", roomNumber=" + roomNumber
                + '}';
    }
}
