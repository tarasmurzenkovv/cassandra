package com.dataart.tmurzenkov.cassandra.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.UserDefinedType;

/**
 * Cassandra UDT 'Address'.
 *
 * @author tmurzenkov
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
}
