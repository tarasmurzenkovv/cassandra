package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.AvailableRoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.AvailableRoomByHotelAndDate;
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
    private final AvailableRoomByHotelAndDateDao availableRoomByHotelAndDateDao;

    /**
     * The below services will be autowired by Spring automatically.
     *
     * @param hotelDao                       {@link HotelDao}
     * @param availableRoomByHotelAndDateDao {@link AvailableRoomByHotelAndDateDao}
     */
    public RoomServiceImpl(HotelDao hotelDao, AvailableRoomByHotelAndDateDao availableRoomByHotelAndDateDao) {
        this.hotelDao = hotelDao;
        this.availableRoomByHotelAndDateDao = availableRoomByHotelAndDateDao;
    }

    @Override
    public AvailableRoomByHotelAndDate addRoomToHotel(AvailableRoomByHotelAndDate availableRoomByHotelAndDate) {
        LOGGER.info("Going to add the new room to the hotel '{}'", availableRoomByHotelAndDate);
        validatePassedRoom(availableRoomByHotelAndDate);
        availableRoomByHotelAndDate.setAvailable(true);
        AvailableRoomByHotelAndDate addedAvailableRoomByHotelAndDate = availableRoomByHotelAndDateDao.insert(availableRoomByHotelAndDate);
        LOGGER.info("Successfully added the new room to the hotel '{}'", addedAvailableRoomByHotelAndDate);
        return addedAvailableRoomByHotelAndDate;
    }

    @Override
    public List<AvailableRoomByHotelAndDate> findFreeRoomsInTheHotel(SearchRequest searchRequest) {
        List<AvailableRoomByHotelAndDate> freeRoomsInHotel = availableRoomByHotelAndDateDao
                .findAvailableRoomsForHotelId(searchRequest.getHotelId(), searchRequest.getStart(), searchRequest.getEnd())
                .stream()
                .filter(AvailableRoomByHotelAndDate::getAvailable)
                .collect(toList());
        if (freeRoomsInHotel.isEmpty()) {
            throw new RecordNotFoundException(format("No free rooms were found for the given request '%s'", searchRequest));
        }
        LOGGER.info("Found the following free rooms '{}'", makeString(freeRoomsInHotel));
        return freeRoomsInHotel;
    }

    private void validatePassedRoom(AvailableRoomByHotelAndDate availableRoomByHotelAndDate) {

        if (null == availableRoomByHotelAndDate) {
            throw new IllegalArgumentException("Cannot add the the room. It is empty. ");
        }
        if (availableRoomByHotelAndDate.getId() == null) {
            final String nullHotelId =
                    format("Hotel id is empty. Cannot add the the room with number '%d' for such hotel. Specify the hotel id",
                    availableRoomByHotelAndDate.getRoomNumber());
            throw new IllegalArgumentException(
                    nullHotelId);
        }
        if (null == hotelDao.findOne(availableRoomByHotelAndDate.getId())) {
            final String cannotFindHotel = format("Cannot find the hotel for the given hotel id '%s'", availableRoomByHotelAndDate.getId());
            throw new RecordNotFoundException(cannotFindHotel);
        }
        if (availableRoomByHotelAndDate.getRoomNumber() == null || 0 == availableRoomByHotelAndDate.getRoomNumber()) {
            throw new IllegalArgumentException(
                    format("Cannot add the the room with number '%d'. ", availableRoomByHotelAndDate.getRoomNumber()));
        }
        if (availableRoomByHotelAndDateDao.exists(availableRoomByHotelAndDate.getCompositeId())) {
            throw new RecordExistsException(format("The room is already inserted in DB. Room info '%s'", availableRoomByHotelAndDate));
        }
    }
}
