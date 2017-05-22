package com.dataart.tmurzenkov.cassandra.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;

/**
 * Cassandra spring data base configuration.
 *
 * @author tmurzenkov
 */
@Configuration
@PropertySource(value = {"classpath:/application.properties"})
public abstract class BaseCassandraConfiguration extends AbstractCassandraConfiguration {
    @Value("${cassandra.contactpoints}")
    protected String contactPoints;
}
