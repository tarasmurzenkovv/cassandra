package com.dataart.tmurzenkov.cassandra.model.entity.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

/**
 * 'HotelsByCity' cassandra entity.
 *
 * @author tmurzenkov
 */
@Data
@NoArgsConstructor
@Table("hotels_by_city")
public class HotelByCity extends BasicEntity {
    @PrimaryKeyColumn(name = "city_name", type = PrimaryKeyType.PARTITIONED)
    private String cityName;
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    /**
     * Create from {@link Hotel}.
     *
     * @param hotel {@link Hotel}
     */
    public HotelByCity(Hotel hotel) {
        this.id = hotel.getId();
        this.cityName = hotel.getAddress().getCity();
    }

    @Override
    public MapId getCompositeId() {
        return BasicMapId.id("hotel_id", this.id).with("city_name", this.cityName);
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
