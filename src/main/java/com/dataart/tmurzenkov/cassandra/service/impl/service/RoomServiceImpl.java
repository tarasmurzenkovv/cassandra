package com.dataart.tmurzenkov.cassandra.service.impl.service;

import com.dataart.tmurzenkov.cassandra.dao.hotel.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.model.dto.SearchRequest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.RoomByHotelAndDate;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.RoomService;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dataart.tmurzenkov.cassandra.model.entity.BookingStatus.FREE;
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
    @Autowired
    private RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Autowired
    private ValidatorService<RoomByHotelAndDate> validatorService;

    @Override
    public RoomByHotelAndDate addRoomToHotel(RoomByHotelAndDate roomByHotelAndDate) {
        LOGGER.info("Going to add the new room to the hotel '{}'", roomByHotelAndDate);
        validatorService.validateInfo(roomByHotelAndDate);
        validatorService.checkIfExists(roomByHotelAndDate);
        roomByHotelAndDate.setBookingStatus(FREE);
        RoomByHotelAndDate addedRoomByHotelAndDate = roomByHotelAndDateDao.insert(roomByHotelAndDate);
        LOGGER.info("Successfully added the new room to the hotel '{}'", addedRoomByHotelAndDate);
        return addedRoomByHotelAndDate;
    }

    @Override
    public List<RoomByHotelAndDate> findFreeRoomsInTheHotel(SearchRequest searchRequest) {
        List<RoomByHotelAndDate> freeRoomsInHotel = roomByHotelAndDateDao
                .findAvailableRoomsForHotelId(searchRequest.getHotelId(), searchRequest.getStart(), searchRequest.getEnd())
                .stream()
                .filter(e -> FREE == e.getBookingStatus())
                .collect(toList());
        if (freeRoomsInHotel.isEmpty()) {
            throw new RecordNotFoundException(format("No free rooms were found for the given request '%s'", searchRequest));
        }
        LOGGER.info("Found the following free rooms '{}'", makeString(freeRoomsInHotel));
        return freeRoomsInHotel;
    }
}
