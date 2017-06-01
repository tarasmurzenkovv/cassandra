CREATE keyspace reservation
WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};

use reservation;

create table if not exists room_booked_by_guest_and_date(
	guest_id uuid,
	booking_date date,
	room_number int,
	hotel_id uuid,
	confirmation_number text,
	primary key ((guest_id,booking_date),hotel_id,  room_number)
)
with
   comment = 'Stores booking information about the specific availableRoomByHotelAndDate for the provided guest id and date.';

create table if not exists room_by_hotel_and_date_and_status(
    hotel_id uuid,
    room_number int,
    booking_date date,
    status text,
    primary key ((hotel_id,booking_date, room_number), booking_date)
)
with clustering
  order by (booking_date desc)
  and comment = 'Stores booking information about the specific availableRoomByHotelAndDate for the provided guest id and date.';