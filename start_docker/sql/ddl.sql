CREATE EXTENSION dblink;

CREATE DATABASE finale;

/*DO
$do$
    BEGIN
        IF EXISTS (SELECT FROM pg_database WHERE datname = 'finale') THEN
            RAISE NOTICE 'Database already exists';  -- optional
        ELSE
            PERFORM dblink_exec('dbname=' || current_database()  -- current db
                , 'CREATE DATABASE finale');
        END IF;
    END
$do$;*/
/*CREATE USER senla_user WITH ENCRYPTED PASSWORD 'password';*/
CREATE USER senla_user WITH PASSWORD 'senla_pass';
GRANT ALL PRIVILEGES ON DATABASE finale TO senla_user;

\connect finale

CREATE SEQUENCE IF NOT EXISTS posts_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
ALTER SEQUENCE posts_id_seq OWNER TO senla_user;

CREATE SEQUENCE IF NOT EXISTS comments_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
ALTER SEQUENCE comments_id_seq OWNER TO senla_user;

CREATE SEQUENCE IF NOT EXISTS messages_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
ALTER SEQUENCE messages_id_seq OWNER TO senla_user;
/*DROP TABLE IF EXISTS users;*/
CREATE TABLE IF NOT EXISTS users
(
    "email" VARCHAR(50) NOT NULL PRIMARY KEY,
    "password" VARCHAR(255),
    "name" VARCHAR(50),
    surname VARCHAR(75),
    phone VARCHAR(10),
    rating INTEGER,
    "role" VARCHAR(25),
    activity BOOLEAN
);

ALTER TABLE users OWNER TO senla_user;

CREATE TABLE IF NOT EXISTS posts
(
    id BIGSERIAL NOT NULL PRIMARY KEY
        DEFAULT nextval('posts_id_seq'),
    user_email VARCHAR(50),
    title VARCHAR(50),
    description TEXT,
    price DECIMAL(12,2),
    promotion DECIMAL(12,2),
    sold BOOLEAN,
    posting_date DATE,
    "category" VARCHAR(50),
    rating INTEGER,
    FOREIGN KEY(user_email) REFERENCES users(email) ON DELETE CASCADE
);

ALTER TABLE posts OWNER TO senla_user;

ALTER SEQUENCE posts_id_seq OWNED BY posts.id;

CREATE TABLE IF NOT EXISTS comments
(
    id BIGSERIAL NOT NULL PRIMARY KEY
        DEFAULT nextval('comments_id_seq'),
    user_email VARCHAR(50),
    post_id BIGSERIAL,
    "content" TEXT,
    "time" TIMESTAMP,
    FOREIGN KEY(user_email) REFERENCES users(email) ON DELETE CASCADE,
    FOREIGN KEY(post_id) REFERENCES posts(id) ON DELETE CASCADE
);

ALTER TABLE comments OWNER TO senla_user;

ALTER SEQUENCE comments_id_seq OWNED BY comments.id;

CREATE TABLE IF NOT EXISTS messages
(
    id BIGSERIAL NOT NULL PRIMARY KEY
        DEFAULT nextval('messages_id_seq'),
    sender_email VARCHAR(50),
    receiver_email VARCHAR(50),
    "content" TEXT,
    "time" TIMESTAMP,
    FOREIGN KEY(sender_email) REFERENCES users(email),
    FOREIGN KEY(receiver_email) REFERENCES users(email)
);

ALTER TABLE messages OWNER TO senla_user;

ALTER SEQUENCE messages_id_seq OWNED BY messages.id;