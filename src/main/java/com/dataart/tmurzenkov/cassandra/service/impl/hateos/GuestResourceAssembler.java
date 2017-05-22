package com.dataart.tmurzenkov.cassandra.service.impl.hateos;

import com.dataart.tmurzenkov.cassandra.controller.GuestController;
import com.dataart.tmurzenkov.cassandra.model.entity.Guest;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * {@link Guest} resource assembler.
 *
 * @author tmurzenkov
 */
@Service
public class GuestResourceAssembler implements ResourceAssembler<Guest, Resource<Guest>> {
    /**
     * Assembles HATEOAS like resource from the provided {@link Room}.
     *
     * @param guest {@link Room} the given java pojo to construct from
     * @return {@link Resource} of type {@link Room}
     */
    @Override
    public Resource<Guest> toResource(Guest guest) {
        Resource<Guest> roomResource = new Resource<>(guest);
        roomResource.add(linkTo(GuestController.class).slash(guest.getId()).withSelfRel());
        return roomResource;
    }
}
