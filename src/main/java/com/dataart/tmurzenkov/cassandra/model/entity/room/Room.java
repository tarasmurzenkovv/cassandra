package com.dataart.tmurzenkov.cassandra.model.entity.room;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * Stores the bookings.
 *
 * @author tmurzenkov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table("room_by_hotel")
public class Room extends BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "room_number", type = CLUSTERED)
    private Integer roomNumber;

    /**
     * Constructs from {@link RoomByHotelAndDate}.
     *
     * @param roomByHotelAndDate {@link RoomByHotelAndDate}
     */
    public Room(RoomByHotelAndDate roomByHotelAndDate) {
        this.id = roomByHotelAndDate.getId();
        this.roomNumber = roomByHotelAndDate.getRoomNumber();
    }

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id)
                .with("roomNumber", this.roomNumber);
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
