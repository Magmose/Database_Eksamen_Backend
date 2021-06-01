DROP TABLE IF EXISTS role_type CASCADE;
DROP TABLE IF EXISTS user_data CASCADE;
DROP TABLE IF EXISTS user_role_log CASCADE;
DROP TABLE IF EXISTS premium_user_data CASCADE;

---

-- ROLE TABLE

CREATE TABLE role_type (
	role_type varchar (20) PRIMARY KEY,
	description varchar (100) NOT NULL
);

INSERT INTO role_type VALUES ('admin', 'admin of users and modereator of posts');
INSERT INTO role_type VALUES ('mod', 'modereator of posts');
INSERT INTO role_type VALUES ('premium', 'premium user');
INSERT INTO role_type VALUES ('std', 'standart free user');




-- USER TABLE

CREATE TABLE user_data (
	id SERIAL PRIMARY KEY,
	username varchar(20) UNIQUE NOT NULL,
	sirname varchar(20) NOT NULL,
	lastname varchar(20) NOT NULL,
	email varchar(30) UNIQUE NOT NULL,
	password varchar(30) NOT NULL,
	birthyear int NOT NULL,
	role_type varchar (20) REFERENCES role_type NOT NULL
	);

INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('Rasmus123', 'rasmus', 'Jacobsen', 'test@r.dk', 'password123', 1995, 'std');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('magmoz', 'magnus', 'klit', 'l@test.dk', '321', 1969, 'admin');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('kimL123', 'kim', 'Larsen', 'kim@L.dk', 'password123', 1995, 'std');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('bjrn34', 'bjarne', 'laursen', 'test@f.dk', '321', 1969, 'std');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('lr95', 'lars', 'rasmussen', 'test@s.dk', '321', 1969, 'premium');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('movie123', 'magnus', 'hansen', 'test@test.dk', '123', 1969, 'premium');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('mathiazz', 'mathias', '...', 'test@w.dk', 'password123', 1995, 'std');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('bjørn345', 'bjørn', 'karlsen', 'test@d.dk', 'password123', 1995, 'std');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('movieMan9000', 'arvid', 'admin', 'arvid@a.dk', 'password123', 1995, 'mod');
INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type) VALUES ('andersk', 'anders', 'kalhauge', 'test@x.dk', '321', 1969, 'std');



-- PREMIUM TABLE
CREATE TABLE premium_user_data (
	id int PRIMARY KEY REFERENCES user_data NOT NULL,
	premium_end_date bigint NOT NULL,
	subsciption_tier varchar(20) NOT NULL,
	card_number bigint NOT NULL
	);

INSERT INTO premium_user_data VALUES (6, 123456789, 'gold', 12345644339);
INSERT INTO premium_user_data VALUES (8, 123456789, 'platinum', 12345644339);



-- VIEWS
CREATE OR REPLACE VIEW full_user_data AS
    SELECT U.*, P.premium_end_date, P.subsciption_tier, P.card_number
	FROM user_data AS U
		LEFT OUTER JOIN premium_user_data AS P ON U.id = P.id;
---	select * from full_user_data;


-- PROCEDURE

CREATE OR REPLACE PROCEDURE create_user (
	_username varchar(20),
	_sirname varchar(20),
	_lastname varchar(20),
	_email varchar(30),
	_password varchar(30),
	_birthyear int,
	_role_type varchar (20),

	_premium_end_date bigint DEFAULT NULL,
	_subsciption_tier varchar(20) DEFAULT NULL,
	_card_number bigint DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
BEGIN

IF _premium_end_date IS NOT NULL AND _subsciption_tier IS NOT NULL AND _card_number IS NOT NULL THEN
	WITH new_user AS (
	    INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type)
		VALUES (_username, _sirname, _lastname, _email, _password, _birthyear, _role_type) RETURNING id
		)
	INSERT INTO premium_user_data (id, premium_end_date, subsciption_tier, card_number) SELECT id,  _premium_end_date, _subsciption_tier, _card_number FROM new_user;


ELSE
	INSERT INTO user_data (username, sirname, lastname, email, password, birthyear, role_type)
		VALUES (_username, _sirname, _lastname, _email, _password, _birthyear, _role_type);

END IF;
END $$;
---
CALL create_user (
	'TEST',
	'FORNAVN',
	'EFTERNAVN',
	'MEAIL@EMAIL.COM',
	'QWERTY',
	6969,
	'std',
	1242234,
	'gold',
	1233423452345

);



CREATE TABLE user_role_log (
	id SERIAL PRIMARY KEY,
	user_id int REFERENCES user_data NOT NULL,
	old_role_type varchar (20) REFERENCES role_type NOT NULL,
	new_role_type varchar (20) REFERENCES role_type NOT NULL,
	timestamp timestamp default current_timestamp
);

INSERT INTO user_role_log(user_id, old_role_type, new_role_type) VALUES (2,'mod', 'admin');



-- TRIGGER
CREATE OR REPLACE FUNCTION user_change()
RETURNS TRIGGER

AS $$
BEGIN
	IF NEW.role_type != OLD.role_type THEN
	INSERT INTO user_role_log(user_id, old_role_type, new_role_type)
		 VALUES(OLD.id , OLD.role_type, NEW.role_type);
	END IF;

	RETURN NEW;

END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS user_role_change ON movieusers;

CREATE TRIGGER user_role_change
  BEFORE UPDATE
  ON user_data
  FOR EACH ROW
  EXECUTE PROCEDURE user_change();
---
