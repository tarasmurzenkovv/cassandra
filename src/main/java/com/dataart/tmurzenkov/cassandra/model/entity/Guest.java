package com.dataart.tmurzenkov.cassandra.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Guest{"
                + "id=" + id
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", emails=" + emails
                + ", phoneNumbers=" + phoneNumbers
                + ", title='" + title + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Guest guest = (Guest) o;

        if (id != null ? !id.equals(guest.id) : guest.id != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(guest.firstName) : guest.firstName != null) {
            return false;
        }
        return lastName != null ? lastName.equals(guest.lastName) : guest.lastName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }
}
