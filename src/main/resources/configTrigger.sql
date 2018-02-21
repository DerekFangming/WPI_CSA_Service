/*Table mappings*/
/*
 * user_details    --- user_detail_hists
 * payments        --- payment_hists
 * survival_guides --- survival_guide_hists
 * users           --- user_hists
 * feeds           --- feed_hists
 * events          --- event_hists
 */

create table User_detail_hists (
	id serial primary key,
	oid integer,
	user_id integer not null,
	name varchar(20),
	nickname varchar(10),
	age integer,
	gender varchar(1),
	location varchar(10),
	whats_up varchar(200),
	birthday varchar(8),
	year varchar(4),
	major varchar(10),
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION User_detail_func() RETURNS trigger AS
$$
BEGIN
	INSERT INTO User_detail_hists
	VALUES (nextval('user_detail_hists_id_seq'::regclass), OLD.id, OLD.user_id, OLD.name, OLD.nickname, OLD.age, OLD.gender, OLD.location, OLD.whats_up, OLD.birthday, OLD.year, OLD.major, substring(TG_OP,1,1), NOW());
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER User_detail_trigger BEFORE UPDATE OR DELETE
ON User_details FOR EACH ROW
EXECUTE PROCEDURE User_detail_func();

create table Payment_hists (
	id serial primary key,
	oid integer,
	type varchar(10),
	mapping_id integer,
	amount decimal(10, 2) not null,
	status varchar(10) not null,
	message varchar(200),
	payer_id integer not null,
	receiver_id integer not null,
	method varchar(100),
	nonce varchar(50),
	created_at timestamp without time zone not null,
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION Payment_func() RETURNS trigger AS
$$
BEGIN
	INSERT INTO Payment_hists
	VALUES (nextval('payment_hists_id_seq'::regclass), OLD.id, OLD.type, OLD.mapping_id, OLD.amount, OLD.status, OLD.message, OLD.payer_id, OLD.receiver_id, OLD.method, OLD.nonce, OLD.created_at, substring(TG_OP,1,1), NOW());
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER Payment_trigger BEFORE UPDATE OR DELETE
ON Payments FOR EACH ROW
EXECUTE PROCEDURE Payment_func();

create table Survival_guide_hists (
	id serial primary key,
	oid integer,
	title varchar(30) not null,
	content text,
	parent_id integer,
	position integer not null,
	created_at timestamp without time zone not null default now(),
	owner_id integer not null default 0,
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION Survival_guide_func() RETURNS trigger AS
$$
BEGIN
	IF TG_OP = 'DELETE' OR OLD.title != NEW.title OR OLD.content != New.content THEN
		INSERT INTO Survival_guide_hists
		VALUES (nextval('survival_guide_hists_id_seq'::regclass), OLD.id, OLD.title, OLD.content, OLD.parent_id, OLD.position, OLD.created_at, OLD.owner_id, substring(TG_OP,1,1), NOW());
	ELSE
		INSERT INTO Survival_guide_hists
		VALUES (nextval('survival_guide_hists_id_seq'::regclass), OLD.id, OLD.title, OLD.content, OLD.parent_id, OLD.position, OLD.created_at, OLD.owner_id, 'L', NOW());
	END IF;
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER Survival_guide_trigger BEFORE UPDATE OR DELETE
ON Survival_guides FOR EACH ROW
EXECUTE PROCEDURE Survival_guide_func();

create table User_hists (
	id serial primary key,
	oid integer,
	username varchar(32) not null,
	password varchar(32) not null,
	email_confirmed boolean not null default false,
	role_id integer not null default 99,
	updated_by integer,
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION User_func() RETURNS trigger AS
$$
BEGIN
	IF OLD.role_id != NEW.role_id THEN
		INSERT INTO User_hists
		VALUES (nextval('user_hists_id_seq'::regclass), OLD.id, OLD.username, OLD.password, OLD.email_confirmed, OLD.role_id, OLD.updated_by, substring(TG_OP,1,1), NOW());
	END IF;
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;

END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER User_trigger BEFORE UPDATE OR DELETE
ON Users FOR EACH ROW
EXECUTE PROCEDURE User_func();

create table Feed_hists (
	id serial primary key,
	oid integer,
	title varchar(100),
	type varchar(10),
	body text,
	owner_id integer not null,
	enabled boolean not null default true,
	created_at timestamp without time zone not null,
	updated_by integer,
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION Feed_func() RETURNS trigger AS
$$
BEGIN
	INSERT INTO Feed_hists
	VALUES (nextval('feed_hists_id_seq'::regclass), OLD.id, OLD.title, OLD.type, OLD.body, OLD.owner_id, OLD.enabled, OLD.created_at, OLD.updated_by, substring(TG_OP,1,1), NOW());
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER Feed_trigger BEFORE UPDATE OR DELETE
ON Feeds FOR EACH ROW
EXECUTE PROCEDURE Feed_func();

create table Event_hists (
	id serial primary key,
	oid integer,
	type varchar(10),
	mapping_id integer,
	title varchar(100),
	description varchar(1000),
	start_time timestamp without time zone,
	end_time timestamp without time zone,
	location varchar(300),
	fee decimal(10, 2),
	owner_id integer not null,
	created_at timestamp without time zone not null,
	ticket_template_id integer,
	active boolean not null default true,
	message varchar(100),
	ticket_balance integer not null default 0,
	updated_by integer,
	action varchar(1) not null default 'U',
	action_date timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION Event_func() RETURNS trigger AS
$$
BEGIN
	IF OLD.ticket_balance - NEW.ticket_balance != 1 THEN
		INSERT INTO Event_hists
		VALUES (nextval('event_hists_id_seq'::regclass), OLD.id, OLD.type, OLD.mapping_id, OLD.title, OLD.description, OLD.start_time, OLD.end_time, OLD.location, OLD.fee, OLD.owner_id, OLD.created_at, OLD.ticket_template_id, OLD.active, OLD.message, OLD.ticket_balance, OLD.updated_by, substring(TG_OP,1,1), NOW());
	END IF;
	IF TG_OP = 'DELETE' THEN
		RETURN OLD;
	ELSE
		RETURN NEW;
	END IF;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER Event_trigger BEFORE UPDATE OR DELETE
ON Events FOR EACH ROW
EXECUTE PROCEDURE Event_func();

/**

The following triggers have not been deployed yet.


*/
