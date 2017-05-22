package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    public List<Room> findFreeRoomsInTheHotel(Date start, Date end, UUID hotelId) {
        return null;
    }
}
