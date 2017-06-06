package com.dataart.tmurzenkov.cassandra.model.entity.hotel;

import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import com.datastax.driver.core.DataType;
import com.datastax.driver.mapping.annotations.FrozenValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
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
 */
@Data
@Table("hotels")
public class Hotel extends BasicEntity {
    @PrimaryKeyColumn(name = "hotel_id", type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.CLUSTERED)
    private String name;
    @Column(value = "phone")
    private String phone;
    @CassandraType(type = DataType.Name.UDT, userTypeName = "address")
    @FrozenValue
    private Address address = new Address();

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return new BasicMapId().with("id", this.id).with("name", this.name);
    }
}
