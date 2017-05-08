package com.dataart.tmurzenkov.cassandra.model.entity;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.UserDefinedType;

/**
 * Cassandra UDT 'Address'.
 *
 * @author tmurzenkov
 */
@UserDefinedType(value = "address")
public class Address {
    @Column(value = "street")
    private String street;
    @Column(value = "city")
    private String city;
    @Column(value = "state_or_province")
    private String stateOrProvince;
    @Column(value = "post_code")
    private String postalCode;
    @Column(value = "country")
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        if (street != null ? !street.equals(address.street) : address.street != null) {
            return false;
        }
        if (city != null ? !city.equals(address.city) : address.city != null) {
            return false;
        }
        if (stateOrProvince != null ? !stateOrProvince.equals(address.stateOrProvince) : address.stateOrProvince != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) {
            return false;
        }
        return country != null ? country.equals(address.country) : address.country == null;
    }

    @Override
    public int hashCode() {
        int result = street != null ? street.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (stateOrProvince != null ? stateOrProvince.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{"
                + "street='" + street + '\''
                + ", city='" + city + '\''
                + ", stateOrProvince='" + stateOrProvince + '\''
                + ", postal_code='" + postalCode + '\''
                + ", country='" + country + '\''
                + '}';
    }
}
