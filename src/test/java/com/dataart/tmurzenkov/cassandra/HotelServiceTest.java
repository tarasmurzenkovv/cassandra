package com.dataart.tmurzenkov.cassandra;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.service.impl.HotelServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.validator.RecordExistsValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * UTs for the {@link HotelServiceImpl}.
 *
 * @author tmurzenkov
 */
@RunWith(MockitoJUnitRunner.class)
public class HotelServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private HotelDao hotelDao;
    @Mock
    private HotelByCityDao hotelByCityDao;
    @Mock
    private RecordExistsValidator validatorService;
    @InjectMocks
    private HotelServiceImpl sut;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldStoreDataInHotelByCityAndInHotelTables() {
        final Hotel newHotel = new Hotel();
        final HotelByCity newHotelByCity = new HotelByCity(newHotel);
        when(validatorService.withRepository(eq(hotelDao))).thenReturn(validatorService);
        when(validatorService.doValidate(eq(newHotel))).thenReturn(newHotel);
        when(hotelDao.save(eq(newHotel))).thenReturn(newHotel);
        when(hotelByCityDao.save(eq(newHotelByCity))).thenReturn(newHotelByCity);
        sut.addHotel(newHotel);
        verify(validatorService).withRepository(eq(hotelDao));
        verify(validatorService).doValidate(eq(newHotel));
        verify(hotelDao).save(eq(newHotel));
        verify(hotelByCityDao).save(eq(newHotelByCity));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldThrowExceptionIfSuchHotelExists() {
        final Hotel newHotel = new Hotel();
        final HotelByCity newHotelByCity = new HotelByCity(newHotel);
        final String message = format("Such record exists '%s'", newHotel.getId());
        when(validatorService.withRepository(eq(hotelDao))).thenReturn(validatorService);
        doThrow(new RecordExistsException(message)).when(validatorService).doValidate(newHotel);
        thrown.expect(RecordExistsException.class);
        sut.addHotel(newHotel);
        thrown.expectMessage(is(message));
        verify(validatorService).withRepository(eq(hotelDao));
        verify(validatorService).doValidate(eq(newHotel));
        verify(hotelDao, never()).save(eq(newHotel));
        verify(hotelByCityDao, never()).save(eq(newHotelByCity));
    }

    @Test
    public void shouldFindAllHotelsInTheCity() {
        final String cityName = "London";
        List<Hotel> hotels = buildHotelList(cityName);
        List<UUID> hotelIds = hotels.stream().map(Hotel::getId).collect(toList());
        List<HotelByCity> hotelsByCity = buildHotelsByCity(hotels);
        when(hotelByCityDao.findAllHotelIdsInTheCity(eq(cityName))).thenReturn(hotelsByCity);
        when(hotelDao.findAllHotelsByTheirIds(eq(hotelIds))).thenReturn(hotels);

        sut.findAllHotelsInTheCity(cityName);

        verify(hotelDao).findAllHotelsByTheirIds(eq(hotelIds));
        verify(hotelByCityDao).findAllHotelIdsInTheCity(eq(cityName));
    }

    private List<Hotel> buildHotelList(String cityName) {
        List<Hotel> hotels = new ArrayList<>();
        final Address address = new Address();
        address.setCity(cityName);
        final Hotel hilton = new Hotel();
        hilton.setAddress(address);
        final Hotel redisson = new Hotel();
        redisson.setAddress(address);
        hotels.add(hilton);
        hotels.add(redisson);
        return hotels;
    }

    private List<HotelByCity> buildHotelsByCity(List<Hotel> hotels) {
        return hotels.stream().map(HotelByCity::new).collect(toList());
    }
}
