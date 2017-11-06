package com.dataart.tmurzenkov.cassandra.controller;

import com.dataart.tmurzenkov.cassandra.service.impl.ExceptionInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Abstract class to init the {@link MockMvc}.
 *
 * @param <T> any class that has annotation {@link org.springframework.web.bind.annotation.RestController}
 * @author Taras_Murzenkov
 */
class AbstractControllerUnitTest<T> {

    /**
     * Inits {@link MockMvc} with {@link MappingJackson2HttpMessageConverter} and {@link LocalValidatorFactoryBean}.
     *
     * @param controller any class that has annotation {@link org.springframework.web.bind.annotation.RestController}
     * @return {@link MockMvc}
     */
    MockMvc init(T controller) {
        final RestController annotation = controller.getClass().getAnnotation(RestController.class);
        if (null == annotation) {
            throw new RuntimeException("This test suite is solely designed for the rest controllers. ");
        }
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return standaloneSetup(controller)
                .setControllerAdvice(new ExceptionInterceptor())
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }
}
