DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users(
  id SERIAL PRIMARY KEY,
  name VARCHAR(30),
  password TEXT,
  email VARCHAR(30));
/*
INSERT INTO users (name, email) VALUES ('Alexandr', 'alexandr@example.com'), ('Sergey', 'sergey@example.com');
*/
INSERT INTO users (name, password, email) VALUES ('user1','passwd1','user1@example.com'), ('user2', 'passwd2', 'user2@example.com');

DROP TABLE IF EXISTS sessions;
CREATE TABLE IF NOT EXISTS sessions(
    id uuid UNIQUE PRIMARY KEY,
    user_id INTEGER,
    user_name VARCHAR (50),
    expires_in timestamp);