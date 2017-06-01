package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final RoomByHotelAndDateDao roomByHotelAndDateDao;

    /**
     * The below services will be autowired by Spring automatically.
     *
     * @param hotelDao                       {@link HotelDao}
     * @param roomByHotelAndDateDao {@link RoomByHotelAndDateDao}
     */
    public RoomServiceImpl(HotelDao hotelDao, RoomByHotelAndDateDao roomByHotelAndDateDao) {
        this.hotelDao = hotelDao;
        this.roomByHotelAndDateDao = roomByHotelAndDateDao;
    }

    @Override
    public RoomByHotelAndDate addRoomToHotel(RoomByHotelAndDate roomByHotelAndDate) {
        LOGGER.info("Going to add the new room to the hotel '{}'", roomByHotelAndDate);
        validatePassedRoom(roomByHotelAndDate);
        roomByHotelAndDate.setAvailable(true);
        RoomByHotelAndDate addedRoomByHotelAndDate = roomByHotelAndDateDao.insert(roomByHotelAndDate);
        LOGGER.info("Successfully added the new room to the hotel '{}'", addedRoomByHotelAndDate);
        return addedRoomByHotelAndDate;
    }

    @Override
    public List<RoomByHotelAndDate> findFreeRoomsInTheHotel(SearchRequest searchRequest) {
        List<RoomByHotelAndDate> freeRoomsInHotel = roomByHotelAndDateDao
                .findAvailableRoomsForHotelId(searchRequest.getHotelId(), searchRequest.getStart(), searchRequest.getEnd())
                .stream()
                .filter(RoomByHotelAndDate::getAvailable)
                .collect(toList());
        if (freeRoomsInHotel.isEmpty()) {
            throw new RecordNotFoundException(format("No free rooms were found for the given request '%s'", searchRequest));
        }
        LOGGER.info("Found the following free rooms '{}'", makeString(freeRoomsInHotel));
        return freeRoomsInHotel;
    }

    private void validatePassedRoom(RoomByHotelAndDate roomByHotelAndDate) {

        if (null == roomByHotelAndDate) {
            throw new IllegalArgumentException("Cannot add the the room. It is empty. ");
        }
        if (roomByHotelAndDate.getId() == null) {
            final String nullHotelId =
                    format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                    roomByHotelAndDate.getRoomNumber());
            throw new IllegalArgumentException(
                    nullHotelId);
        }
        if (null == hotelDao.findOne(roomByHotelAndDate.getId())) {
            final String cannotFindHotel = format("Cannot find the hotel for the given hotel id '%s'", roomByHotelAndDate.getId());
            throw new RecordNotFoundException(cannotFindHotel);
        }
        if (roomByHotelAndDate.getRoomNumber() == null || 0 == roomByHotelAndDate.getRoomNumber()) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. ", roomByHotelAndDate.getRoomNumber()));
        }
        if (roomByHotelAndDateDao.exists(roomByHotelAndDate.getCompositeId())) {
            throw new RecordExistsException(format("The room is already inserted in DB. Room info '%s'", roomByHotelAndDate));
        }
    }
}
