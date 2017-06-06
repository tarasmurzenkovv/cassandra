package com.dataart.tmurzenkov.cassandra.service.impl;

import com.dataart.tmurzenkov.cassandra.model.entity.BasicEntity;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Assembles HATEOUS resources.
 *
 * @param <T> {@link BasicEntity}
 * @param <C> any controller class
 * @author tmurzenkov
 */
@Service
public class ServiceResourceAssembler<T extends BasicEntity, C> implements ResourceAssembler<T, Resource<T>> {
    private Class<?> controllerClass;

    /**
     * Adds controller class to extract the base uri.
     *
     * @param controllerClass controller class
     * @return this
     */
    public ServiceResourceAssembler<T, C> withController(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
        return this;
    }

    /**
     * Transforms {@link List} of the type {@link BasicEntity} to the list of {@link Resource}.
     *
     * @param basicEntities {@link List}
     * @return {@link List}
     */
    public List<Resource<T>> toResource(List<T> basicEntities) {
        return basicEntities.stream().map(this::toResource).collect(toList());
    }


    /**
     * Transforms {@link List} of the type {@link BasicEntity} to the list of {@link Resource}.
     *
     * @param basicEntities {@link Set}
     * @return {@link List}
     */
    public List<Resource<T>> toResource(Set<T> basicEntities) {
        return basicEntities.stream().map(this::toResource).collect(toList());
    }

    @Override
    public Resource<T> toResource(T basicEntity) {
        Resource<T> resource = new Resource<>(basicEntity);
        resource.add(linkTo(controllerClass).slash(basicEntity.getId()).withSelfRel());
        return resource;
    }
}
