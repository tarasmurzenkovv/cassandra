package com.dataart.tmurzenkov.cassandra;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.service.impl.HotelServiceImpl;
import com.dataart.tmurzenkov.cassandra.service.impl.RecordValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
    private RecordValidator validatorService;
    @InjectMocks
    private HotelServiceImpl sut;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldStoreDataInHotelByCityAndInHotelTables() {
/*        final Hotel newHotel = new Hotel();
        final HotelByCity newHotelByCity = new HotelByCity(newHotel);
        when(validatorService.withCondition(eq(hotelDao))).thenReturn(validatorService);
        when(validatorService.doValidate(eq(newHotel))).thenReturn(validatorService);
        when(hotelDao.save(eq(newHotel))).thenReturn(newHotel);
        when(hotelByCityDao.save(eq(newHotelByCity))).thenReturn(newHotelByCity);
        sut.addHotel(newHotel);
        verify(validatorService).withCondition(eq(hotelDao));
        verify(validatorService).doValidate(eq(newHotel));
        verify(hotelDao).save(eq(newHotel));
        verify(hotelByCityDao).save(eq(newHotelByCity));*/


    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldThrowExceptionIfSuchHotelExists() {
 /*       final Hotel newHotel = new Hotel();
        final HotelByCity newHotelByCity = new HotelByCity(newHotel);
        final String message = format("Such record exists '%s'", newHotel.getId());
        when(validatorService.withCondition(eq(hotelDao))).thenReturn(validatorService);
        doThrow(new RecordExistsException(message)).when(validatorService).doValidate(newHotel);
        thrown.expect(RecordExistsException.class);
        sut.addHotel(newHotel);
        thrown.expectMessage(is(message));
        verify(validatorService).withCondition(eq(hotelDao));
        verify(validatorService).doValidate(eq(newHotel));
        verify(hotelDao, never()).save(eq(newHotel));
        verify(hotelByCityDao, never()).save(eq(newHotelByCity));*/
    }

    @Test
    public void shouldFindAllHotelsInTheCity() {
 /*       final String cityName = "London";
        List<Hotel> hotels = buildHotelList(cityName);
        List<UUID> hotelIds = hotels.stream().map(Hotel::getId).collect(toList());
        List<HotelByCity> hotelsByCity = buildHotelsByCity(hotels);
        when(hotelByCityDao.findAllHotelIdsInTheCity(eq(cityName))).thenReturn(hotelsByCity);
        when(hotelDao.findAllHotelsByTheirIds(eq(hotelIds))).thenReturn(hotels);

        sut.findAllHotelsInTheCity(cityName);

        verify(hotelDao).findAllHotelsByTheirIds(eq(hotelIds));
        verify(hotelByCityDao).findAllHotelIdsInTheCity(eq(cityName));*/
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
