package com.dataart.tmurzenkov.cassandra.controller.uri;

/**
 * Guest controller REST api URIs.
 *
 * @author tmurzenkov
 */
public interface GuestUris {
    String ADD_GUEST = "/api/add/guest";
    String ADD_BOOKING = "/api/add/booking";
    String ROOMS_BY_GUEST_AND_DATE = "/api/get/roombyguest/{guestId}/{date}";
}
