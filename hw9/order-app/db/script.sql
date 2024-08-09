DROP TABLE IF EXISTS orders;
CREATE TABLE IF NOT EXISTS orders(
    id SERIAL PRIMARY KEY,
    idempotency_key TEXT
);

DROP TABLE IF EXISTS payments;
CREATE TABLE IF NOT EXISTS payments
(
    id SERIAL PRIMARY KEY,
    order_id int NOT NULL UNIQUE,
    amount int
);

DROP TABLE IF EXISTS goods;
CREATE TABLE IF NOT EXISTS goods
(
    id serial PRIMARY KEY,
    name text NOT NULL,
    price int
);

DROP TABLE IF EXISTS goods_reservations;
CREATE TABLE IF NOT EXISTS goods_reservations
(
    id serial PRIMARY KEY,
    good_id int NOT NULL,
    order_id int
);

INSERT INTO goods (name, price) VALUES ('товар 1', 100);
INSERT INTO goods (name, price) VALUES ('товар 2', 200);
INSERT INTO goods (name, price) VALUES ('товар 3', 300);
INSERT INTO goods (name, price) VALUES ('товар 4', 400);


DROP TABLE IF EXISTS couriers;
CREATE TABLE IF NOT EXISTS couriers
(
    id serial PRIMARY KEY,
    name text NOT NULL
);


DROP TABLE IF EXISTS courier_reservations;
CREATE TABLE IF NOT EXISTS courier_reservations
(
    id serial PRIMARY KEY,
    courier_id int NOT NULL,
    order_id int,
    destination text
);

INSERT INTO couriers (name) VALUES ('курьер 1');
INSERT INTO couriers (name) VALUES ('курьер 2');
INSERT INTO couriers (name) VALUES ('курьер 3');
INSERT INTO couriers (name) VALUES ('курьер 4');




/*DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
  id SERIAL PRIMARY KEY,
  login VARCHAR(30),
  password TEXT,
  firstname VARCHAR(30),
  lastname VARCHAR(30),
  email VARCHAR(30));*/
/*
INSERT INTO users (name, email) VALUES (    'Alexandr', 'alexandr@example.com'), ('Sergey', 'sergey@example.com');
*/
/*
INSERT INTO users (login, password, email) VALUES ('user1','passwd1','user1@example.com'), ('user2', 'passwd2', 'user2@example.com');
*/
/*
DROP TABLE IF EXISTS sessions;
CREATE TABLE IF NOT EXISTS sessions(
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    token uuid,
    user_agent VARCHAR (50),
    ip_address VARCHAR(30),
    expires timestamp,
    created timestamp);

DROP TABLE IF EXISTS accounts;
CREATE TABLE IF NOT EXISTS accounts (
      id SERIAL PRIMARY KEY,
      owner_id INTEGER NOT NULL,
      balance INTEGER
);
*/

/*
CREATE UNIQUE INDEX idx_accounts_owner_unique ON accounts(owner_id);
 */
/*
DROP TABLE IF EXISTS orders;
CREATE TABLE IF NOT EXISTS orders (
        id SERIAL PRIMARY KEY,
        creator_id INTEGER NOT NULL,
        title VARCHAR(50),
        price INTEGER,
        state VARCHAR(30)
);

DROP TABLE IF EXISTS notifications;
CREATE TABLE IF NOT EXISTS notifications (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL,
        text text
);
*/