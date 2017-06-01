create keyspace hotel with replication={'class':'SimpleStrategy', 'replication_factor':3};

use hotel;

create type address(
	street text,
	city text,
	state_or_province text,
	post_code text,
	country text
);

create table if not exists guest(
  guest_id uuid,
  first_name text,
  last_name text,
  emails set<text>,
  phone_numbers set<text>,
  title text,
  primary key ((guest_id))
) with comment = 'Stores guest information. ';


create table if not exists hotels(
  hotel_id uuid,
  name text,
  phone text,
  address frozen<address>,
  primary key ((hotel_id), name)
) with comment = 'Stores hotel information. ';

create table if not exists hotels_by_city(
  city_name text,
  hotel_id uuid,
  primary key ((city_name), hotel_id)
) with comment = 'Queries hotels ids for the given city. ';

create table if not exists available_rooms_by_hotel_date(
  hotel_id uuid,
  date date,
  room_number int,
  is_available text,
  primary key ((hotel_id), date, room_number)
) with comment = 'Finds available roomByHotelAndDates by hotel/date. ';
