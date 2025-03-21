DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    id          integer GENERATED ALWAYS AS IDENTITY,
    name        text        NOT NULL,
    description text        NULL,
    created_at  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (name)
);