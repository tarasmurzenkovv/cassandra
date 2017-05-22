package com.dataart.tmurzenkov.cassandra.service.impl.hateos;

import com.dataart.tmurzenkov.cassandra.controller.HotelController;
import com.dataart.tmurzenkov.cassandra.model.entity.hotel.Hotel;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * {@link Hotel} resource assembler.
 *
 * @author tmurzenkov
 */
@Service
public class HotelResourceAssembler implements ResourceAssembler<Hotel, Resource<Hotel>> {

    /**
     * Assembles HATEOAS like resource from the provided {@link Hotel}.
     *
     * @param hotel {@link Hotel} the given java pojo to construct from
     * @return {@link Resource} of type {@link Hotel}
     */
    @Override
    public Resource<Hotel> toResource(Hotel hotel) {
        Resource<Hotel> hotelResource = new Resource<>(hotel);
        hotelResource.add(linkTo(HotelController.class).slash(hotel.getId()).withSelfRel());
        return hotelResource;
    }
}
