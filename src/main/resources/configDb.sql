create table users (
	id serial primary key,
	username varchar(32) NOT NULL,
	password varchar(32) NOT NULL,
	access_token varchar(255),
	veri_token varchar(255),
	created_at timestamp without time zone NOT NULL,
	email_confirmed boolean NOT NULL DEFAULT false,
	salt varchar(32) NOT NULL,
	role_id integer NOT NULL DEFAULT 99
);

create table images (
	id serial primary key,
	location varchar(50) NOT NULL,
	type varchar(10),
	type_mapping_id integer,
	owner_id integer NOT NULL,
	created_at timestamp without time zone NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	title varchar(50)
);

create table relationships (
	id serial primary key,
	sender_id integer NOT NULL,
	receiver_id integer NOT NULL,
	confirmed boolean NOT NULL DEFAULT false,
	type varchar(10),
	created_at timestamp without time zone NOT NULL
);

create table user_details (
	id serial primary key,
	user_id integer NOT NULL,
	name varchar(20),
	nickname varchar(10),
	age integer,
	gender varchar(1),
	location varchar(10),
	whats_up varchar(200),
	birthday varchar(8),
	year varchar(4),
	major varchar(10)
);

create table feeds (
	id serial primary key,
	title varchar(100),
	type varchar(10),
	body text,
	owner_id integer NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	created_at timestamp without time zone NOT NULL
);

create table comments (
	id serial primary key,
	body varchar(400),
	mentioned_user_id integer,
	type varchar(10),
	type_mapping_id integer,
	owner_id integer NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	created_at timestamp without time zone NOT NULL
);

create table sg (
	id serial primary key,
	menu_id integer not null,
	title varchar(1000),
	content varchar(25000),
	created_at timestamp without time zone
);

create table wc_reports (
	id serial primary key,
	user_id integer,
	menu_id integer not null,
	email varchar(50),
	report text,
	created_at timestamp without time zone
);

create table wc_app_versions (
	id serial primary key,
	app_version varchar(10) not null,
	status varchar(3) not null,
	title varchar(50),
	message varchar(500),
	updates text
);

create table wc_articles (
	id serial primary key,
	title text not null,
	article text not null,
	menu_id integer not null,
	user_id integer not null,
	created_at timestamp without time zone not null
);

create table events (
	id serial primary key,
	type varchar(10),
	mapping_id integer,
	title varchar(100),
	description varchar(1000),
	start_time timestamp without time zone,
	end_time timestamp without time zone,
	location varchar(300),
	fee decimal(10, 2),
	owner_id integer NOT NULL,
	created_at timestamp without time zone NOT NULL,
	ticket_template_id integer,
	active boolean not null default true,
	message varchar(100),
	ticket_balance integer not null default 0
);

create table ticket_templates (
	id serial primary key,
	location varchar(50) not null,
	serial_number integer not null,
	description varchar(50) not null,
	logo_text varchar(30),
	bg_color varchar(10),
	owner_id integer not null,
	created_at timestamp without time zone not null
);

create table tickets (
	id serial primary key,
	template_id int not null,
	type varchar(10),
	mapping_id integer,
	location varchar(100) not null,
	owner_id integer not null,
	created_at timestamp without time zone not null
);

create table payments (
	id serial primary key,
	type varchar(10),
	mapping_id integer,
	amount decimal(10, 2) not null,
	status varchar(10) not null,
	message varchar(200),
	payer_id integer not null,
	receiver_id integer not null,
	method varchar(10),
	nonce varchar(50),
	created_at timestamp without time zone not null
);


-- Needs to be run on Prod    Also run app version script!

ALTER TABLE users RENAME auth_token TO access_token;
ALTER TABLE users ALTER COLUMN timezone_offset SET DEFAULT 99;
ALTER TABLE users RENAME timezone_offset to role_id;


