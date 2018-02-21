create table cmain (
	id serial primary key,
	ehr integer,
	phr integer,
	temp decimal(5, 2),
	spo2 integer,
	created_at timestamp without time zone not null
);

create table ppg (
	id serial primary key,
	mid integer,
	ird integer,
	rd integer
);

create table ecg (
	id serial primary key,
	mid integer,
	ed integer
);