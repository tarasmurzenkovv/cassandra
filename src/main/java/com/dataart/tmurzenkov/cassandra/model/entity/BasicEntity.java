package com.dataart.tmurzenkov.cassandra.model.entity;

import org.springframework.data.cassandra.repository.MapId;

import java.util.UUID;

/**
 * Basic Cassandra entity.
 *
 * @author tmurzenkov
 * @see org.springframework.data.cassandra.repository.support.BasicMapId
 */
public interface BasicEntity {

    /**
     * Must return the new composite id.
     *
     * @return {@link MapId}
     */
    MapId getCompositeId();

    /**
     * Serves as the partitioning key.
     *
     * @return {@link UUID}
     */
    UUID getId();
}
