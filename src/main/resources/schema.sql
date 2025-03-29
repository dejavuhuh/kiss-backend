-- DROP TABLE IF EXISTS permission;
-- DROP TABLE IF EXISTS role;
-- DROP TABLE IF EXISTS session;
-- DROP TABLE IF EXISTS "user";

CREATE TABLE IF NOT EXISTS "user"
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    username     text        NOT NULL,
    password     text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS session
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    token        text        NOT NULL,
    user_id      integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_time timestamptz NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (token),
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS session_history
(
    id           integer     NOT NULL,
    user_id      integer     NOT NULL,
    reason       text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS role
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

CREATE TABLE IF NOT EXISTS permission
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    parent_id    integer     NULL,
    type         text        NOT NULL,
    code         text        NOT NULL,
    name         text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE NULLS NOT DISTINCT (parent_id, code),
    FOREIGN KEY (parent_id) REFERENCES permission
);

CREATE TABLE IF NOT EXISTS role_permission_mapping
(
    role_id       integer NOT NULL,
    permission_id integer NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role,
    FOREIGN KEY (permission_id) REFERENCES permission
);

CREATE TABLE IF NOT EXISTS user_role_mapping
(
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "user",
    FOREIGN KEY (role_id) REFERENCES role
);
