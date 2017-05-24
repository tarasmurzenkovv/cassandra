package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;

/**
 * {@link RoomService} implementation.
 *
 * @author tmurzenkov
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;

    @Override
    public void addRoomToHotel(Room room) {
        Hotel one = hotelDao.findOne(room.getHotelId());
        if (one == null) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. There is no such hotel with id '%s'",
                            room.getRoomNumber(), room.getHotelId()));
        }
        if (room.getHotelId() == null) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. Specify the hotel id", room.getRoomNumber()));
        }
        roomDao.save(room);
    }

    @Override
    public Set<Room> findFreeRoomsInTheHotel(Date start, Date end, UUID hotelId) {
/*        Set<Room> bookedRooms = roomByHotelAndDateDao.findBookedRoomInHotelForDate(hotelId,
fromMillisSinceEpoch(start.getTime()), fromMillisSinceEpoch(end.getTime()))
                .stream()
                .map(bookedRoom -> new Room(bookedRoom.getHotelId(), bookedRoom.getRoomNumber()))
                .collect(toSet());
        Set<Room> allRooms = roomDao.findAllRoomsInHotel(hotelId);
        allRooms.removeAll(bookedRooms);*/
        return null;
    }
}
