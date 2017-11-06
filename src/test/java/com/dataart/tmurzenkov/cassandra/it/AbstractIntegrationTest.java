package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.dao.GuestDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByHotelAndDateDao;
import com.dataart.tmurzenkov.cassandra.dao.HotelByCityDao;
import com.dataart.tmurzenkov.cassandra.dao.RoomByGuestAndDateDao;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Abstract integration test.
 *
 * @author tmurzenkov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
@EnableWebMvc
public abstract class AbstractIntegrationTest {
    @Autowired
    protected GuestDao guestDao;
    @Autowired
    protected HotelDao hotelDao;
    @Autowired
    protected RoomByHotelAndDateDao roomByHotelAndDateDao;
    @Autowired
    protected HotelByCityDao hotelByCityDao;
    @Autowired
    protected RoomByGuestAndDateDao roomByGuestAndDateDao;
    @Autowired
    private WebApplicationContext wac;
    protected MockMvc mockMvc;

    /**
     * Creates {@link MockMvc}.
     */
    @Before
    public void init() {
        mockMvc = webAppContextSetup(this.wac).build();
    }

    /**
     * Clear all entries.
     */
    @After
    public void clearDbEntries() {
        guestDao.deleteAll();
        hotelDao.deleteAll();
        hotelByCityDao.deleteAll();
        roomByHotelAndDateDao.deleteAll();
        roomByGuestAndDateDao.deleteAll();
    }
}
