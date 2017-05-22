package com.dataart.tmurzenkov.cassandra.service.impl.hateos;

import com.dataart.tmurzenkov.cassandra.controller.RoomController;
import com.dataart.tmurzenkov.cassandra.model.entity.room.Room;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * {@link Room} resource assembler.
 *
 * @author tmurzenkov
 */
@Service
public class RoomResourceAssembler implements ResourceAssembler<Room, Resource<Room>> {

    /**
     * Assembles HATEOAS like resource from the provided {@link Room}.
     *
     * @param room {@link Room} the given java pojo to construct from
     * @return {@link Resource} of type {@link Room}
     */
    @Override
    public Resource<Room> toResource(Room room) {
        Resource<Room> roomResource = new Resource<>(room);
        roomResource.add(linkTo(RoomController.class).slash(room.getRoomNumber()).withSelfRel());
        return roomResource;
    }
}
