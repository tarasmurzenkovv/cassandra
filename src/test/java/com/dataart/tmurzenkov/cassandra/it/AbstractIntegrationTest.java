package com.dataart.tmurzenkov.cassandra.it;

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
    private WebApplicationContext wac;
    protected MockMvc mockMvc;

    /**
     * Inits {@link MockMvc}.
     */
    @Before
    public void init() {
        mockMvc = webAppContextSetup(this.wac).build();
    }

    /**
     * Clear all entries.
     */
    @After
    public abstract void clearDbEntries();
}
