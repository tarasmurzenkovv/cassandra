create keyspace reservation with replication={'class':'SimpleStrategy', 'replication_factor':3};
use reservation;
create type address(
	street text,
	city text,
	state_or_province text,
	post_code text,
	country text
);
create table reservations_by_hotel_date(
	hotel_id uuid,
	guest_id uuid,
	start_date date,
	end_date date,
	room_number smallint,
	confirmation_number text,
	primary key ((hotel_id, start_date), room_number)
)with comment = 'Find find reservations by hotel and start date';

create materialized view reservation_by_confirmation as
	select * from reservation.reservations_by_hotel_date
	where confirmation_number is not null
	and hotel_id is not null
	and start_date is not null
	and room_number is not null
	primary key (confirmation_number, hotel_id, start_date, room_number)
	with comment ='Find all reserved room numbers by the hotel id';

create table reservations_by_guest(
	hotel_id uuid,
	guest_id uuid,
	start_date date,
	end_date date,
	room_number smallint,
	confirmation_number text,
	guest_last_name text,
	primary key ((guest_last_name), hotel_id)
)with comment = 'Find find reservations by guest name';

create table guests(
	guest_id uuid,
	first_name text,
	last_name text,
	title text,
	emails set<text>,
	phone_numbers set<text>,
	addresses map<text, frozen<address>>,
	confirm_number text,
	primary key((guest_id))
)with comment = 'Finds guest by guest id';