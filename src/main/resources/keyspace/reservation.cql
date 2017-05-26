CREATE keyspace reservation
WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};

use reservation;

create table if not exists room_booked_by_guest_and_date(
guest_id uuid,
booking_date date,
room_number int,
hotel_id uuid,
confirmation_number text,
primary key ((guest_id), booking_date)
)
with clustering
  order by (booking_date desc) and comment = 'Stores booking information about the specific room for the provided guest id and date.';

create table if not exists room_booked_by_guest_and_date(
guest_id uuid,
booking_date date,
room_number int,
hotel_id uuid,
confirmation_number int,
primary key ((guest_id), booking_date)
)
with clustering
  order by (booking_date desc) and comment = 'Stores booking information about the specific room for the provided guest id and date.';