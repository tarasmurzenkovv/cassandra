create table guest(
	id uuid,
	guest_name text,
	guest_surname text,
	guest_email text,
	guest_telephones set<text>,
	primary key ((id), guest_name, guest_surname, guest_email)
);