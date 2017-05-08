package com.dataart.tmurzenkov.cassandra.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Cassandra spring configuration.
 *
 * @author tmurzenkov
 */
@Configuration
@PropertySource(value = {"classpath:/application.properties"})
@EnableCassandraRepositories(basePackages = {"com.dataart.tmurzenkov.cassandra.dao"})
public class CassandraConfiguration extends AbstractCassandraConfiguration {
    @Value("${cassandra.contactpoints}")
    private String contactPoints;
    @Value("${cassandra.port}")
    private Integer port;
    @Value("${cassandra.keyspace}")
    private String keySpace;

    /**
     * Setups the cluster connection factory bean from the provided contact points.
     *
     * @return {@link CassandraClusterFactoryBean}.
     */
    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(contactPoints);
        return cluster;
    }

    /**
     * Defines the mapping context for the Cassandra.
     *
     * @return {@link CassandraMappingContext}
     * @see BasicCassandraMappingContext
     */
    @Bean
    public CassandraMappingContext mappingContext() {
        BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), keySpace));
        return mappingContext;
    }

    /**
     * Central Cassandra specific converter interface from Object to Row.
     *
     * @return {@link CassandraConverter}
     */
    @Bean
    public CassandraConverter converter() {
        return new MappingCassandraConverter(mappingContext());
    }

    /**
     * Configures {@link CassandraSessionFactoryBean}.
     *
     * @return {@link CassandraSessionFactoryBean}
     */
    @Bean
    public CassandraSessionFactoryBean session() {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cluster().getObject());
        session.setKeyspaceName(keySpace);
        session.setConverter(converter());
        session.setSchemaAction(SchemaAction.NONE);
        return session;
    }

    /**
     * Configures {@link CassandraOperations} from {@link CassandraTemplate}.
     *
     * @return {@link CassandraOperations}
     */
    @Override
    protected String getKeyspaceName() {
        return keySpace;
    }
}
