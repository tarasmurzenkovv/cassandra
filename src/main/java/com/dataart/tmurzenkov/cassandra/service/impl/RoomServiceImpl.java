package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.booking.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus.BOOKED;
import static com.dataart.tmurzenkov.cassandra.service.impl.RecordValidator.validator;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.makeString;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * {@link RoomService} implementation.
 *
 * @author tmurzenkov
 */
@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);
    private final HotelDao hotelDao;
    private final RoomDao roomDao;
    private final RoomByHotelAndDateDao roomByHotelAndDateDao;

    /**
     * The below services will be autowired by Spring automatically.
     *
     * @param hotelDao              {@link HotelDao}
     * @param roomDao               {@link RoomDao}
     * @param roomByHotelAndDateDao {@link RoomByHotelAndDateDao}
     */
    public RoomServiceImpl(HotelDao hotelDao, RoomDao roomDao, RoomByHotelAndDateDao roomByHotelAndDateDao) {
        this.hotelDao = hotelDao;
        this.roomDao = roomDao;
        this.roomByHotelAndDateDao = roomByHotelAndDateDao;
    }

    @Override
    public Room addRoomToHotel(Room room) {
        LOGGER.info("Going to register the new room '{}'", room);
        validatePassedRoom(room);
        Hotel one = hotelDao.findOne(room.getId());
        validateHotel(one);
        Room addedRoom = roomDao.insert(room);
        LOGGER.info("Successfully registered the new room '{}'", addedRoom);
        return addedRoom;
    }

    @Override
    public List<Room> findFreeRoomsInTheHotel(SearchRequest searchRequest) {
        List<Room> freeRooms = roomByHotelAndDateDao
                .findBookedRoomInHotelForDate(searchRequest.getHotelId(), searchRequest.getStart(), searchRequest.getEnd())
                .stream()
                .filter(roomByHotelAndDate -> BOOKED != roomByHotelAndDate.getStatus())
                .map(Room::new).collect(toList());
        if (freeRooms.isEmpty()) {
            throw new RecordNotFoundException(format("No free rooms were found for the given request '%s'", searchRequest));
        }
        LOGGER.info("Found the following free rooms '{}'", makeString(freeRooms));
        return freeRooms;
    }

    private void validatePassedRoom(Room room) {
        if (null == room) {
            throw new IllegalArgumentException("Cannot add the the room. It is empty. ");
        }
        if (room.getId() == null) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. Specify the hotel id", room.getRoomNumber()));
        }
        if (room.getRoomNumber() == null || 0 == room.getRoomNumber()) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. ", room.getRoomNumber()));
        }
    }

    private void validateHotel(Hotel hotel) {
        final String message = format("Cannot find the hotel information for the hotel id '%s'", hotel.getId());
        validator()
                .withCondition(h -> !hotelDao.exists(h.getCompositeId()))
                .onConditionFailureThrow(() -> new RecordNotFoundException(message))
                .doValidate(hotel);
    }
}
