package com.dataart.tmurzenkov.cassandra.configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Swagger bean configuration.
 *
 * @author tmurzenkov
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Configures swagger as spring bean.
     *
     * @return {@link Docket}
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .directModelSubstitute(LocalDateTime.class, String.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dataart.tmurzenkov.cassandra.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Configures basic API information.
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        return new ApiInfo("Rest API for the cassandra course",
                "RESTFul API implementation for DA internal cassandra course",
                "", "", "myeaddress@company.com", "",
                "https://gitlab.dataart.com/training.Cassandra/tmurzenkov-dbs05.git");
    }
}
