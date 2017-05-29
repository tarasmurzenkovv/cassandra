package com.dataart.tmurzenkov.cassandra.configuration;

import com.dataart.tmurzenkov.cassandra.configuration.database.BookingCassandraConfiguration;
import com.dataart.tmurzenkov.cassandra.configuration.database.HotelCassandraConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Configures the {@link WebContextConfiguration}.
 *
 * @author tmurzenkov
 */
@EnableWebMvc
@Configuration
@Import({SwaggerConfiguration.class, HotelCassandraConfiguration.class, BookingCassandraConfiguration.class})
@ComponentScan("com.dataart.tmurzenkov.cassandra")
public class WebContextConfiguration extends WebMvcConfigurerAdapter {

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
     * Adds resource handlers for the Swagger.
     *
     * @param registry {@link ResourceHandlerRegistry}
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                break;
            }
        }
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
