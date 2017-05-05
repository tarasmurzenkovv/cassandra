create table city {
    city_name text,
    country text,
    primary key (city_name)
};

create table hotel(
    hotel_id uuid,
    hotel_name text,
    hotel_address text,
    hotel_city text,
    hotel_country text,
    hotel_phone text,
    hotel_zip text,
    primary key (hotel_id,hotel_name, hotel_address, hotel_city, hotel_country));

create table room {
    room_number text,
    is_booked boolean,
    primary key (room_number)
};

create table guest {
    guest_name text,
    primary key (name)
};



-- simple one-to-many relationship 1 city many hotels
create table hotels_in_the_city {
    hotel_name text,
    hotel_address text,
    city_name text,
    primary key (hotel_name, city_name)
};
-- simple one-to-many relationship 1 hotel many rooms
create table rooms_in_the_hotel {
    hotel_name text,
    room_number text,
    primary key (hotel_name, room_number,reserved_date)
};

-- simple many-to-many relationship many guests may book many rooms
create table rooms_booked_by_guests {
    hotel_name text,
    room_number text,
    guest_name text,
    is_booked boolean,
    reserved_date date,
    primary key (hotel_name, room_number,guest_name, reserved_date)
};




writes are not expensive, do them!

/*
* CLIENT ID	5f79b95100cb4c2fa8b46a8119ac6cc7
SUPPORT EMAIL	murzenkt@gmail.com
CLIENT STATUS	Sandbox Mode

226901405.5f79b95.9de7dacf4e3a456da2c7eabc23b46c8b
https://www.instagram.com/ggetbraine/?__a=1 -- user info
226901405
* */


@Service
public class GuestResourceAssembler implements ResourceAssembler<Guest, Resource<Guest>> {
@Override
public Resource<Guest> toResource(Guest guest) {
Resource<Guest> guestResource = new Resource<>(guest);
guestResource.add(linkTo(GuestController.class).slash(guest.getId()).withSelfRel());
return guestResource;
}
}
