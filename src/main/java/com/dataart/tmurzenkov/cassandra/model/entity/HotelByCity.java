package com.dataart.tmurzenkov.cassandra.model.entity;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

/**
 * 'HotelsByCity' cassandra entity.
 *
 * @author tmurzenkov
 */
@Table("hotels_by_city")
public class HotelByCity {
    @PrimaryKeyColumn(value = "city", ordinal = OrdinalConstants.FIRST, type = PrimaryKeyType.PARTITIONED)
    private String city;
    @PrimaryKeyColumn(value = "hotel_id", ordinal = OrdinalConstants.SECOND, type = PrimaryKeyType.CLUSTERED)
    private UUID hotelId;

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
        this.hotelId = hotel.getId();
        this.city = hotel.getAddress().getCity();
    }

    public UUID getHotelId() {
        return hotelId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

        return hotelId != null ? hotelId.equals(that.hotelId) : that.hotelId == null;
    }

    @Override
    public int hashCode() {
        return hotelId != null ? hotelId.hashCode() : 0;
    }
}
