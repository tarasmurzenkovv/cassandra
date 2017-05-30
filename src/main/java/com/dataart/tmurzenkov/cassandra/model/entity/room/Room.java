package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

/**
 * Room cassandra entity.
 *
 * @author tmurzenkov
 */

@Table("rooms_by_hotel")
public class Room extends BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "room_number", type = PrimaryKeyType.CLUSTERED)
    private Integer roomNumber;

    /**
     * Constructor.
     */
    public Room() {
    }

    /**
     * Constructs from the provided hotel id and room number.
     *
     * @param id         {@link UUID}
     * @param roomNumber {@link Integer}
     */
    public Room(UUID id, Integer roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
    }

    /**
     * Constructs from {@link RoomByGuestAndDate}.
     *
     * @param roomByGuestAndDate {@link RoomByGuestAndDate}
     */
    public Room(RoomByGuestAndDate roomByGuestAndDate) {
        this(roomByGuestAndDate.getHotelId(), roomByGuestAndDate.getRoomNumber());
    }

    /**
     * Constructs from {@link RoomByHotelAndDate}.
     *
     * @param roomByHotelAndDate {@link RoomByHotelAndDate}
     */
    public Room(RoomByHotelAndDate roomByHotelAndDate) {
        this(roomByHotelAndDate.getId(), roomByHotelAndDate.getRoomNumber());
    }


    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id).with("roomNumber", this.roomNumber);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Room room = (Room) o;

        if (id != null ? !id.equals(room.id) : room.id != null) {
            return false;
        }
        return roomNumber != null ? roomNumber.equals(room.roomNumber) : room.roomNumber == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roomNumber != null ? roomNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Room{"
                + "hotel_id=" + id
                + ", roomNumber=" + roomNumber
                + '}';
    }
}
