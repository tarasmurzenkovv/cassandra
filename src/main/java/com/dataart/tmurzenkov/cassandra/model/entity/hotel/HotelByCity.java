package com.dataart.tmurzenkov.cassandra.model.entity;

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
    @PrimaryKeyColumn(value = "city", type = PrimaryKeyType.PARTITIONED, forceQuote = true)
    private String city;
    @PrimaryKeyColumn(value = "id", type = PrimaryKeyType.CLUSTERED)
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
        this.city = hotel.getAddress().getCity();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
