package com.dataart.tmurzenkov.cassandra.model.entity;

import org.springframework.data.cassandra.repository.MapId;
import org.springframework.data.cassandra.repository.support.BasicMapId;

import java.util.UUID;

/**
 * Basic Cassandra entity.
 *
 * @author tmurzenkov
 * @see BasicMapId
 */
public abstract class BasicEntity {

    /**
     * Must return the composite id.
     *
     * @return {@link MapId}
     */
    public abstract MapId getCompositeId();

    /**
     * Must return the id.
     *
     * @return {@link UUID}
     */
    public abstract UUID getId();
}
