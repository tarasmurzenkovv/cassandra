package com.dataart.tmurzenkov.cassandra.service.impl.service;

import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.service.util.CollectionUtils.difference;
import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.makeString;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

/**
 * {@link RoomService} implementation.
 *
 * @author tmurzenkov
 */
@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private ValidatorService<Room> validatorService;

    @Override
    public Room addRoomToHotel(Room room) {
        LOGGER.info("Going to add the new room to the hotel '{}'", room);
        validatorService.validateInfo(room);
        validatorService.checkIfExists(room);
        Room addedRoom = roomDao.insert(room);
        LOGGER.info("Successfully added the new room to the hotel '{}'", addedRoom);
        return addedRoom;
    }

    @Override
    public Set<Room> findFreeRoomsInTheHotel(SearchRequest searchRequest) {
        Set<Room> bookedRoomsInHotel = findAllRoomsBySearchRequest(searchRequest);
        Set<Room> allRoomsInHotel = roomDao.findAllRoomsByHotelId(searchRequest.getHotelId());
        Set<Room> freeRooms = doMakeJoin(searchRequest, bookedRoomsInHotel, allRoomsInHotel);
        LOGGER.info("Found the following free rooms '{}'", makeString(freeRooms));
        return freeRooms;
    }

    private Set<Room> doMakeJoin(final SearchRequest searchRequest, final Set<Room> bookedRoomsInHotel, final Set<Room> allRoomsInHotel) {
        Set<Room> freeRooms = difference(bookedRoomsInHotel, allRoomsInHotel);
        if (freeRooms.isEmpty()) {
            throw new RecordNotFoundException(format("No free rooms were found for the given request '%s'", searchRequest));
        }
        return freeRooms;
    }

    private Set<Room> findAllRoomsBySearchRequest(final SearchRequest searchRequest) {
        final UUID hotelId = searchRequest.getHotelId();
        final LocalDate start = searchRequest.getStart();
        final LocalDate end = searchRequest.getEnd();
        return roomByHotelAndDateDao.findAllRoomsForHotelIdAndPeriod(hotelId, start, end).stream().map(Room::new).collect(toSet());
    }
}
