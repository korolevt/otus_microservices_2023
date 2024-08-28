DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
  id SERIAL PRIMARY KEY,
  login VARCHAR(30),
  password TEXT,
  firstname VARCHAR(30),
  lastname VARCHAR(30),
  email VARCHAR(30));
/*
INSERT INTO users (name, email) VALUES (    'Alexandr', 'alexandr@example.com'), ('Sergey', 'sergey@example.com');
*/
/*
INSERT INTO users (login, password, email) VALUES ('user1','passwd1','user1@example.com'), ('user2', 'passwd2', 'user2@example.com');
*/

DROP TABLE IF EXISTS sessions;
CREATE TABLE IF NOT EXISTS sessions(
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    token uuid,
    user_agent VARCHAR (50), /* Название браузера */
    ip_address VARCHAR(30),
    expires timestamp,
    created timestamp);

DROP TABLE IF EXISTS accounts;
CREATE TABLE IF NOT EXISTS accounts (
      id SERIAL PRIMARY KEY,
      user_id INTEGER NOT NULL,
      balance INTEGER
);

DROP TABLE IF EXISTS payments;
CREATE TABLE IF NOT EXISTS payments
(
    id SERIAL PRIMARY KEY,
    order_id int NOT NULL UNIQUE,
    amount int  /* стоимость заказа */
);


/*
CREATE UNIQUE INDEX idx_accounts_owner_unique ON accounts(owner_id);
 */

DROP TABLE IF EXISTS orders;
CREATE TABLE IF NOT EXISTS orders (
        id SERIAL PRIMARY KEY,
        idempotency_key TEXT,
        user_id INTEGER NOT NULL
);



DROP TABLE IF EXISTS notifications;
CREATE TABLE IF NOT EXISTS notifications (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL,
        message text
);


DROP TABLE IF EXISTS locations;
CREATE TABLE IF NOT EXISTS locations (
    id   SERIAL PRIMARY KEY,
    name text
);

INSERT INTO locations (id, name) VALUES (1, 'Алмазный фонд');
INSERT INTO locations (id, name) VALUES (2, 'ВДНХ');


DROP TABLE IF EXISTS excursions;
CREATE TABLE IF NOT EXISTS excursions (
    id   SERIAL PRIMARY KEY,
    name text,
    duration INTERVAL,
    description TEXT
);

INSERT INTO excursions (id, name, duration, description) VALUES (1, 'Сокровища Алмазного фонда', '1 hours', null);
INSERT INTO excursions (id, name, duration, description) VALUES (2, 'Путеводитель по ВДНХ', '3 hours', null);
INSERT INTO excursions (id, name, duration, description) VALUES (3, 'Космонавтика и Авиация', '2 hours', null);


DROP TABLE IF EXISTS excursion_schedule;
CREATE TABLE IF NOT EXISTS excursion_schedule (
    id            SERIAL PRIMARY KEY,
    location_id   INTEGER,
    excursion_id  INTEGER,
    start_slot    TIMESTAMP,
    price         INTEGER,
    max_count     INTEGER,
    count         INTEGER /* кол-во заказанных */
);

INSERT INTO excursion_schedule (id, location_id, excursion_id, start_slot, price, max_count, count) VALUES (1, 1, 1, '2024-09-09 15:00:00', 1700, 20, 15);
INSERT INTO excursion_schedule (id, location_id, excursion_id, start_slot, price, max_count, count) VALUES (2, 1, 1, '2024-09-09 17:00:00', 1700, 20, 19);
INSERT INTO excursion_schedule (id, location_id, excursion_id, start_slot, price, max_count, count) VALUES (3, 1, 1, '2024-09-09 19:00:00', 1900, 20, 18);
INSERT INTO excursion_schedule (id, location_id, excursion_id, start_slot, price, max_count, count) VALUES (4, 2, 2, '2024-09-09 17:00:00', 700, 50, 45);
INSERT INTO excursion_schedule (id, location_id, excursion_id, start_slot, price, max_count, count) VALUES (5, 2, 3, '2024-09-09 13:00:00', 500, 200, 199);


DROP TABLE IF EXISTS excursion_reservation;
CREATE TABLE IF NOT EXISTS excursion_reservation (
    id                    SERIAL PRIMARY KEY,
    order_id              INTEGER,
    excursion_schedule_id INTEGER,
    count                 INTEGER /* кол-во билетов */
);

