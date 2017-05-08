package com.dataart.tmurzenkov.cassandra.model.entity;

import com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.Set;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.FIRST;
import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.SECOND;
import static com.dataart.tmurzenkov.cassandra.dao.OrdinalConstants.THIRD;
import static org.springframework.cassandra.core.Ordering.DESCENDING;
import static org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED;
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
@Table("guest")
public class Guest {
    @PrimaryKeyColumn(name = "id", type = PARTITIONED, ordering = DESCENDING)
    private UUID id;
    @PrimaryKeyColumn(name = "guest_name", ordinal = FIRST, type = CLUSTERED, ordering = DESCENDING)
    private String guestName;
    @PrimaryKeyColumn(name = "guest_surname", ordinal = SECOND, type = CLUSTERED, ordering = DESCENDING)
    private String guestSurname;
    @PrimaryKeyColumn(name = "guest_email", ordinal = THIRD, type = CLUSTERED, ordering = DESCENDING)
    private String guestEmail;
    @Column("guest_telephones")
    private Set<String> guestTelephones;

    /**
     * Constructor. Internally, it assigns random {@link UUID} to the entity partitioning id.
     */
    public Guest() {
        this.id = UUID.randomUUID();
    }

    @JsonIgnore
    public MapId getCompositeId() {
        return BasicMapId.id()
                .with("id", this.getId())
                .with("name", this.getGuestName())
                .with("guestSurname", this.getGuestSurname())
                .with("guestEmail", this.getGuestEmail());
    }

    @JsonIgnore
    public UUID getId() {
        return this.id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestSurname() {
        return guestSurname;
    }

    public void setGuestSurname(String guestSurname) {
        this.guestSurname = guestSurname;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public Set<String> getGuestTelephones() {
        return guestTelephones;
    }

    public void setGuestTelephones(Set<String> guestTelephones) {
        this.guestTelephones = guestTelephones;
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

        if (guestName != null ? !guestName.equals(guest.guestName) : guest.guestName != null) {
            return false;
        }
        if (guestSurname != null ? !guestSurname.equals(guest.guestSurname) : guest.guestSurname != null) {
            return false;
        }
        return guestEmail != null ? guestEmail.equals(guest.guestEmail) : guest.guestEmail == null;
    }

    @Override
    public int hashCode() {
        int result = guestName != null ? guestName.hashCode() : 0;
        result = 31 * result + (guestSurname != null ? guestSurname.hashCode() : 0);
        result = 31 * result + (guestEmail != null ? guestEmail.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Guest{"
                + "id=" + id
                + ", guestName='" + guestName + '\''
                + ", guestSurname='" + guestSurname + '\''
                + ", guestEmail='" + guestEmail + '\''
                + ", guestTelephones=" + guestTelephones
                + '}';
    }
}
