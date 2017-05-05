package com.dataart.tmurzenkov.cassandra.model.entity;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.ZEROS;
import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.FIRST;
import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.SECOND;
import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.THIRD;
import static org.springframework.cassandra.core.Ordering.DESCENDING;
import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * 'Hotel' cassandra entity.
 * <p>
 * Key points:
 * - it is sufficient to uniquely identify the hotel by cortege (hotel_name, hotel_address, hotel_city, hotel_country), thus
 * this cortege (along with {@link UUID id} -- partitioned key) will be used as the composite primary key;
 * - the partitioned key (how these entities are being distributes across cluster) is the type of {@link UUID};
 * - the clustered key is being presented by the cortege of (hotel_name, hotel_address, hotel_city, hotel_country);
 * - the clustered key cortege values are used in equals and hashcode methods;
 * - the order of appearance is the following: first will go hotel_name, then hotel_address, then hotel_city, then hotel_country;
 *
 * @author tmurzenkov
 * @see OrdinalConstants
 */
@Table("hotel")
public class Hotel implements BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PARTITIONED, ordering = DESCENDING)
    private UUID id;
    @PrimaryKeyColumn(name = "hotel_name", ordinal = ZEROS, type = CLUSTERED)
    private String name;
    @PrimaryKeyColumn(name = "hotel_address", ordinal = FIRST, type = CLUSTERED)
    private String address;
    @PrimaryKeyColumn(name = "hotel_city", ordinal = SECOND, type = CLUSTERED)
    private String city;
    @PrimaryKeyColumn(name = "hotel_country", ordinal = THIRD, type = CLUSTERED)
    private String country;
    @Column("hotel_phone")
    private String phone;
    @Column("hotel_zip")
    private String zip;

    /**
     * Constructor. Internally, it assigns random {@link UUID} to the entity partitioning id.
     */
    public Hotel() {
        this.id = UUID.randomUUID();
    }

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id()
                .with("id", this.getId())
                .with("name", this.getName())
                .with("address", this.getAddress())
                .with("city", this.getCity())
                .with("country", this.getCountry());
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hotel hotel = (Hotel) o;

        if (name != null ? !name.equals(hotel.name) : hotel.name != null) {
            return false;
        }
        if (address != null ? !address.equals(hotel.address) : hotel.address != null) {
            return false;
        }
        if (city != null ? !city.equals(hotel.city) : hotel.city != null) {
            return false;
        }
        return country != null ? country.equals(hotel.country) : hotel.country == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hotel{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", address='" + address + '\''
                + ", city='" + city + '\''
                + ", country='" + country + '\''
                + ", phone='" + phone + '\''
                + ", zip='" + zip + '\''
                + '}';
    }
}
