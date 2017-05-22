package com.dataart.tmurzenkov.cassandra.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Cassandra spring data configuration to work with dao that are located at
 * com.dataart.tmurzenkov.cassandra.dao.reservation .
 *
 * @author tmurzenkov
 */
@EnableCassandraRepositories(basePackages = "com.dataart.tmurzenkov.cassandra.dao.reservation",
        cassandraTemplateRef = "reservationTemplate")
@Configuration
public class ReservationCassandraConfiguration extends BaseCassandraConfiguration {
    @Value("${cassandra.keyspace.reservation}")
    private String reservationKeySpace;

    @Override
    @Bean(name = "reservationTemplate")
    public CassandraAdminOperations cassandraTemplate() throws Exception {
        return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
    }

    @Override
    @Bean(name = "reservationSession")
    public CassandraSessionFactoryBean session() throws ClassNotFoundException {

        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();

        session.setCluster(cluster().getObject());
        session.setConverter(cassandraConverter());
        session.setKeyspaceName(getKeyspaceName());
        session.setSchemaAction(getSchemaAction());
        session.setStartupScripts(getStartupScripts());
        session.setShutdownScripts(getShutdownScripts());

        return session;
    }

    @Override
    protected String getKeyspaceName() {
        return reservationKeySpace;
    }
}
