package com.dataart.tmurzenkov.cassandra.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.Set;
import java.util.UUID;

import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * 'Guest' cassandra entity.
 *
 * @author tmurzenkov
 */
@Data
@NoArgsConstructor
@Table("guest")
public class Guest extends BasicEntity {
    @PrimaryKeyColumn(name = "guest_id", type = PARTITIONED)
    private UUID id;
    @Column(value = "first_name")
    private String firstName;
    @Column(value = "last_name")
    private String lastName;
    @Column(value = "emails")
    private Set<String> emails;
    @Column(value = "phone_numbers")
    private Set<String> phoneNumbers;
    @Column(value = "title")
    private String title;

    @Override
    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id("id", this.id);
    }
}
