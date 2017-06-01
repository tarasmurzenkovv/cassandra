package com.dataart.tmurzenkov.cassandra.service.impl.validation;

import com.dataart.tmurzenkov.cassandra.dao.hotel.GuestDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.dataart.tmurzenkov.cassandra.service.util.StringUtils.isEmpty;
import static java.lang.String.format;

/**
 * Implementation of the {@link ValidatorService} for the {@link Guest}.
 *
 * @author tmurzenkov
 */
@Service
public class GuestValidatorServiceImpl implements ValidatorService<Guest> {
    @Autowired
    private GuestDao guestDao;

    @Override
    public void validateInfo(Guest guest) {
        if (null == guest) {
            throw new IllegalArgumentException("Cannot register the empty guest info. ");
        }
        if (null == guest.getId()) {
            throw new IllegalArgumentException("Cannot register guest info with empty id. ");
        }
        if (isEmpty(guest.getFirstName())) {
            throw new IllegalArgumentException("Cannot register guest info with empty first name. ");
        }
        if (isEmpty(guest.getLastName())) {
            throw new IllegalArgumentException("Cannot register guest info with empty last name. ");
        }
    }

    @Override
    public void checkIfExists(Guest guest) {
        final String exceptionMessage = format("The guest information is already stored in DB. "
                + "Guest id: '%s', name: '%s', surname: '%s'", guest.getId(), guest.getFirstName(), guest.getLastName());
        if (guestDao.exists(guest.getCompositeId())) {
            throw new RecordExistsException(exceptionMessage);
        }
    }
}
