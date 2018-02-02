create table User_detail_hists (
	id integer,
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
	VALUES (OLD.id, OLD.user_id, OLD.name, OLD.nickname, OLD.age, OLD.gender, OLD.location, OLD.whats_up, OLD.birthday, OLD.year, OLD.major, substring(TG_OP,1,1), NOW());
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
	id integer,
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
	VALUES (OLD.id, OLD.type, OLD.mapping_id, OLD.amount, OLD.status, OLD.message, OLD.payer_id, OLD.receiver_id, OLD.method, OLD.nonce, OLD.created_at, substring(TG_OP,1,1), NOW());
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
	id integer,
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
	INSERT INTO Survival_guide_hists
	VALUES (OLD.id, OLD.title, OLD.content, OLD.parent_id, OLD.position, OLD.created_at, OLD.owner_id, substring(TG_OP,1,1), NOW());
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