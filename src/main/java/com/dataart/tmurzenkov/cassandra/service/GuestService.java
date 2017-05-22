package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;

import java.util.List;
import java.util.UUID;

/**
 * Interface that provides the API to:
 * - add a new guest, who is going to book a room <code>GuestService.performBooking</code>.
 * - perform the registration (expresses the guest willingness) to book the room <code>GuestService.registerNewGuest</code>.
 * - find all booked rooms <code>GuestService.findBookedRoomsForTheGuestId</code>.
 *
 * @author tmurzenkov
 */
public interface GuestService {

    /**
     * Performs the soft marking that the given {@link Room} would be booked by {@link Guest} in the given {@link Hotel}.
     *
     * @param room  {@link Room}
     * @param hotel {@link Hotel}
     * @param guest {@link Guest}
     */
    void performBooking(Room room, Hotel hotel, Guest guest);

    /**
     * Registers the new {@link Guest} in the system.
     *
     * @param guest {@link Guest}
     */
    void registerNewGuest(Guest guest);

    /**
     * Finds all booked {@link Room}s in by the provided {@link UUID} guest id.
     *
     * @param guestId {@link UUID}
     * @return {@link List} of {@link Room}
     */
    List<Room> findBookedRoomsForTheGuestId(UUID guestId);
}
