CREATE TABLE "user"
(
    id       bigint GENERATED ALWAYS AS IDENTITY,
    username text NOT NULL,
    password text NOT NULL,
    avatar   text,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE role
(
    id          bigint GENERATED ALWAYS AS IDENTITY,
    name        text NOT NULL,
    description text,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE user_role_mapping
(
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

CREATE TABLE menu
(
    id        bigint GENERATED ALWAYS AS IDENTITY,
    parent_id bigint,
    name      text    NOT NULL,
    title     text    NOT NULL,
    "order"   integer NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES menu (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON menu (parent_id, name) NULLS NOT DISTINCT;

CREATE TABLE role_menu_mapping
(
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu (id) ON DELETE CASCADE
);