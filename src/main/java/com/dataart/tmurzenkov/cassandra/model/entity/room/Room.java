package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

/**
 * Room cassandra entity.
 *
 * @author tmurzenkov
 */

@Table("rooms_by_hotel")
public class Room {
    @PrimaryKeyColumn(name = "hotel_id", ordinal = OrdinalConstants.ZEROS, type = PrimaryKeyType.PARTITIONED)
    private UUID hotelId;
    @PrimaryKeyColumn(name = "room_number", ordinal = OrdinalConstants.SECOND, type = PrimaryKeyType.CLUSTERED)
    private Integer roomNumber;

    /**
     * Constructor.
     */
    public Room() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Room room = (Room) o;

        if (hotelId != null ? !hotelId.equals(room.hotelId) : room.hotelId != null) {
            return false;
        }

        return roomNumber != null ? !roomNumber.equals(room.roomNumber) : room.roomNumber != null;
    }

    @Override
    public int hashCode() {
        int result = hotelId != null ? hotelId.hashCode() : 0;
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Room{"
                + "hotel_id=" + hotelId
                + ", roomNumber=" + roomNumber
                + '}';
    }
}
