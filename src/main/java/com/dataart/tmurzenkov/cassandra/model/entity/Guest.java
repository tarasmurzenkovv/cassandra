package com.dataart.tmurzenkov.cassandra.model.entity;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Set;
import java.util.UUID;

import static org.springframework.cassandra.core.Ordering.DESCENDING;
import static org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED;

/**
 * 'Guest' cassandra entity.
 * <p>
 * Key points:
 * - it is sufficient to uniquely identify the hotel by cortege (guest_name, guest_surname, guest_email), thus
 * this cortege (along with {@link UUID id} -- partitioned key) will be used as the composite primary key;
 * - the partitioned key (how these entities are being distributes across cluster) is the type of {@link UUID};
 * - the clustered key is being presented by the cortege of (guest_name, guest_surname, hotel_city, guest_email);
 * - the clustered key cortege values are used in equals and hashcode methods;
 * - the order of appearance is the following: first will go guest_name, then guest_surname then guest_email;
 *
 * @author tmurzenkov
 * @see OrdinalConstants
 */
@Table("guests")
public class Guest {
    @PrimaryKeyColumn(name = "guest_id", type = PARTITIONED, ordering = DESCENDING)
    private UUID id;
    @Column(value = "first_name")
    private String firstName;
    @Column(value = "last_name")
    private String lastName;
    @Column(value = "confirm_number")
    private String confirmNumber;
    @Column(value = "emails")
    private Set<String> emails;
    @Column(value = "phone_numbers")
    private Set<String> phoneNumbers;
    @Column(value = "title")
    private String title;

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

    public String getConfirmNumber() {
        return confirmNumber;
    }

    public void setConfirmNumber(String confirmNumber) {
        this.confirmNumber = confirmNumber;
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
}
