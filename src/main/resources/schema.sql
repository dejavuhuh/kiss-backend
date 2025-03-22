DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user"
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    username     text        NOT NULL,
    password     text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE session
(
    token        text PRIMARY KEY,
    user_id      integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_time timestamptz NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TABLE role
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    name         text        NOT NULL,
    description  text        NULL,
    creator_id   integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (name),
    FOREIGN KEY (creator_id) REFERENCES "user"
);