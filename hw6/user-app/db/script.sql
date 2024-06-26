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
/*
CREATE UNIQUE INDEX idx_accounts_owner_unique ON accounts(owner_id);
 */

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
