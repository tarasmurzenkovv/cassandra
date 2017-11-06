package com.dataart.tmurzenkov.cassandra.it;

import com.dataart.tmurzenkov.cassandra.configuration.CassandraConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * For integration tests.
 *
 * @author tmurzenkov
 */
@EnableWebMvc
@Configuration
@Import({CassandraConfiguration.class})
@ComponentScan("com.dataart.tmurzenkov.cassandra")
public class TestConfiguration extends WebMvcConfigurerAdapter {
    /**
     * Enables the default servlet handling.
     *
     * @param configurer {@link DefaultServletHandlerConfigurer}
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * Configures property placeholder.
     *
     * @return {@link PropertySourcesPlaceholderConfigurer}
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
