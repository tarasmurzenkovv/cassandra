package com.dataart.tmurzenkov.cassandra.model.entity.hotel;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.datastax.driver.core.DataType;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

/**
 * 'Hotel' cassandra entity.
 *
 * @author tmurzenkov
 * @see OrdinalConstants
 */
@Table("hotels")
public class Hotel implements BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", ordinal = OrdinalConstants.ZEROS, type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    @Column(value = "name")
    private String name;
    @Column(value = "phone")
    private String phone;
    @CassandraType(type = DataType.Name.UDT, userTypeName = "address")
    @FrozenValue
    private Address address = new Address();

    /**
     * Constructor. Internally, it assigns random {@link UUID} to the entity partitioning id.
     */
    public Hotel() {
        this.id = UUID.randomUUID();
    }

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return new BasicMapId().with("id", this.id);
    }

    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
        if (phone != null ? !phone.equals(hotel.phone) : hotel.phone != null) {
            return false;
        }
        return address != null ? address.equals(hotel.address) : hotel.address == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hotel{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", phone='" + phone + '\''
                + ", address=" + address
                + '}';
    }
}
