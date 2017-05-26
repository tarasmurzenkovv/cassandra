package com.dataart.tmurzenkov.cassandra.controller.uri;

/**
 * Hotel controller REST api URIs.
 *
 * @author tmurzenkov
 */
public interface HotelUris {
    String HOTELS_IN_THE_CITY = "/api/get/{city}";
    String ADD_HOTEL = "/api/add/hotel";
}
