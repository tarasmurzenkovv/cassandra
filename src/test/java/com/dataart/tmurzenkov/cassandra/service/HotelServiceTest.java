package com.dataart.tmurzenkov.cassandra.service;

import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.hotel.HotelDao;
import com.dataart.tmurzenkov.cassandra.model.entity.Address;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.HotelByCity;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordExistsException;
import com.dataart.tmurzenkov.cassandra.model.exception.RecordNotFoundException;
import com.dataart.tmurzenkov.cassandra.service.impl.HotelServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.dataart.tmurzenkov.cassandra.TestUtils.buildAddress;
import static com.dataart.tmurzenkov.cassandra.TestUtils.buildHotel;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

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
    @Captor
    private ArgumentCaptor<HotelByCity> hotelByCityArgumentCaptor;
    @Captor
    private ArgumentCaptor<Hotel> hotelArgumentCaptor;
    @InjectMocks
    private HotelServiceImpl sut;

    @Test
    public void shouldStoreDataInHotelByCityAndInHotelTables() {
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final HotelByCity expectedHotelByCity = new HotelByCity(expectedHotelToAdd);

        when(hotelDao.exists(expectedHotelToAdd.getCompositeId())).thenReturn(false);
        when(hotelDao.insert(eq(expectedHotelToAdd))).thenReturn(expectedHotelToAdd);
        when(hotelByCityDao.insert(eq(expectedHotelByCity))).thenReturn(expectedHotelByCity);

        final Hotel actualHotelToAdd = sut.addHotel(expectedHotelToAdd);

        verify(hotelDao).insert(hotelArgumentCaptor.capture());
        verify(hotelByCityDao).insert(hotelByCityArgumentCaptor.capture());
        verify(hotelDao).exists(eq(expectedHotelToAdd.getCompositeId()));

        final HotelByCity actualHotelByCity = hotelByCityArgumentCaptor.getValue();
        assertEquals(expectedHotelByCity, actualHotelByCity);
        assertEquals(expectedHotelToAdd, actualHotelToAdd);
        assertEquals(hotelArgumentCaptor.getValue(), actualHotelToAdd);
    }

    @Test
    public void shouldThrowExceptionIfSuchHotelExists() {
        final UUID hotelId = UUID.randomUUID();
        final Address hotelAddress = buildAddress();
        final Hotel expectedHotelToAdd = buildHotel(hotelId, hotelAddress);
        final String message = format("Such hotel information is already added to the data base '%s'", expectedHotelToAdd);

        when(hotelDao.exists(expectedHotelToAdd.getCompositeId())).thenReturn(true);
        thrown.expectMessage(message);
        thrown.expect(RecordExistsException.class);

        sut.addHotel(expectedHotelToAdd);

        verify(hotelDao, never()).insert(any());
        verify(hotelByCityDao).insert(any());
    }

    @Test
    public void shouldFindAllHotelsInTheCity() {
        final String cityName = "London";
        final List<Hotel> expectedHotels = buildHotelList(cityName);
        final List<HotelByCity> hotelIdsByCity = buildHotelsByCity(expectedHotels);

        when(hotelByCityDao.findAllHotelIdsInTheCity(eq(cityName))).thenReturn(hotelIdsByCity);
        expectedHotels.forEach(expectedHotel -> when(hotelDao.findOne(eq(expectedHotel.getId()))).thenReturn(expectedHotel));

        List<Hotel> actualAllHotelsInTheCity = sut.findAllHotelsInTheCity(cityName);

        verify(hotelByCityDao).findAllHotelIdsInTheCity(eq(cityName));
        verify(hotelDao, times(expectedHotels.size())).findOne(any(UUID.class));
        assertFalse(actualAllHotelsInTheCity.isEmpty());
        assertEquals(expectedHotels.size(), actualAllHotelsInTheCity.size());
        assertTrue(actualAllHotelsInTheCity.containsAll(expectedHotels));
        assertTrue(expectedHotels.containsAll(actualAllHotelsInTheCity));
    }

    @Test
    public void shouldThrowExceptionIfNoHotelsWereFoundInTheCity() {
        final String cityName = "London";
        final String exceptionMessage = format("Cannot find hotels for the given city '%s'", cityName);

        when(hotelByCityDao.findAllHotelIdsInTheCity(eq(cityName))).thenReturn(emptyList());
        thrown.expectMessage(exceptionMessage);
        thrown.expect(RecordNotFoundException.class);

        sut.findAllHotelsInTheCity(cityName);
        verify(hotelDao, never()).insert(any());
        verify(hotelDao, never()).save(any(Hotel.class));

        verify(hotelByCityDao, never()).insert(any());
        verify(hotelByCityDao, never()).save(any(HotelByCity.class));
    }


    @Test
    public void shouldThrowExceptionForEmptyCityName() {
        final String cityName = "";
        final String exceptionMessage = "Cannot find the hotels for the empty city name";

        thrown.expectMessage(exceptionMessage);
        thrown.expect(IllegalArgumentException.class);

        sut.findAllHotelsInTheCity(cityName);
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
