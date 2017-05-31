package com.dataart.tmurzenkov.cassandra.model.entity.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
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
@Table("hotels_by_city")
public class HotelByCity extends BasicEntity {
    @PrimaryKeyColumn(name = "city_name", type = PrimaryKeyType.PARTITIONED)
    private String cityName;
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    /**
     * Default constructor.
     */
    public HotelByCity() {

    }

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HotelByCity that = (HotelByCity) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "HotelByCity{"
                + "cityName='" + cityName + '\''
                + ", id=" + id
                + '}';
    }
}
