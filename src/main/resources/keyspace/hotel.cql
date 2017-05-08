create keyspace hotel with replication={'class':'SimpleStrategy', 'replication_factor':3};

use hotel;

create type address(
	street text,
	city text,
	state_or_province text,
	post_code text,
	country text
);
create table hotels_by_city(
    hotel_name text,
    city text,
    primary key ((city), hotel_id)
) with comment = 'Find hotels by the city name';

create table hotels_by_poi(
	poi_name text,
	hotel_id uuid,
	name text,
	phone text,
	address frozen<address>,
	primary key ((poi_name), hotel_id)
)with comment = 'Find hotels near the give point of interest (poi)' and clustering order by (hotel_id ASC);

create table hotels(
	hotel_id uuid,
	name text,
	phone text,
	address frozen<address>,
	primary key ((hotel_id))
)with comment = 'Information about the hotel';

create table pois_by_hotels(
	poi_name text,
	hotel_id uuid,
	description text,
	primary key ((hotel_id), poi_name)
)with comment = 'Finds the points of interest (poi) for the given hotel';

create table available_rooms_by_hotel_date(
	hotel_id uuid,
	date date,
	room_number smallint,
	is_available boolean,
	primary key ((hotel_id), date, room_number)
)with comment = 'Finds the available rooms by hotel/date';

create table amenities_by_room(
	hotel_id uuid,
	room_number smallint,
	amenity_name text,
	description text,
	primary key ((hotel_id, room_number), amenity_name)
)with comment = 'Finds the amenities in the given room';

create index city_name on hotel.hotels(address);