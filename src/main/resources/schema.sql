-- DROP TABLE IF EXISTS "order";
-- DROP TABLE IF EXISTS subscription_plan;
-- DROP TABLE IF EXISTS permission_application_permission_mapping;
-- DROP TABLE IF EXISTS permission_application;
-- DROP TABLE IF EXISTS config_history;
-- DROP TABLE IF EXISTS config;
-- DROP TABLE IF EXISTS migration_history;
-- DROP TABLE IF EXISTS issue;
-- DROP TABLE IF EXISTS user_role_mapping;
-- DROP TABLE IF EXISTS role_permission_mapping;
-- DROP TABLE IF EXISTS permission_audit_log;
-- DROP TABLE IF EXISTS permission;
-- DROP TABLE IF EXISTS role;
-- DROP TABLE IF EXISTS session_history;
-- DROP TABLE IF EXISTS session;
-- DROP TABLE IF EXISTS account;
-- DROP TABLE IF EXISTS feishu_user;
-- DROP TABLE IF EXISTS "user";

CREATE TABLE IF NOT EXISTS "user"
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    display_name text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS feishu_user
(
    id      text    NOT NULL,
    user_id integer NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS account
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    user_id      integer     NOT NULL,
    username     text        NOT NULL,
    password     text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username),
    FOREIGN KEY (user_id) REFERENCES "user"
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
    UNIQUE (code),
    FOREIGN KEY (parent_id) REFERENCES permission
);

CREATE TABLE IF NOT EXISTS permission_audit_log
(
    id                integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    permission_id     integer     NOT NULL REFERENCES permission,
    operation         text        NOT NULL,
    operation_details jsonb       NULL,
    operator_id       integer     NOT NULL REFERENCES "user",
    created_time      timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
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

CREATE TABLE IF NOT EXISTS issue
(
    id            integer GENERATED ALWAYS AS IDENTITY,
    title         text        NOT NULL,
    description   text        NOT NULL,
    trace_id      text        NOT NULL,
    request       jsonb       NOT NULL,
    state         text        NOT NULL,
    related_to_id integer     NULL,
    creator_id    integer     NOT NULL,
    created_time  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (creator_id) REFERENCES "user",
    FOREIGN KEY (related_to_id) REFERENCES issue
);

CREATE TABLE IF NOT EXISTS migration_history
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    version      integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (version)
);

CREATE TABLE IF NOT EXISTS config
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    name         text        NOT NULL,
    yaml         text        NULL,
    version      integer     NOT NULL,
    creator_id   integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (name),
    FOREIGN KEY (creator_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS config_history
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    config_id    integer     NOT NULL,
    yaml         text        NULL,
    reason       text        NOT NULL,
    creator_id   integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (config_id) REFERENCES config,
    FOREIGN KEY (creator_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS permission_application
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    reason       text        NOT NULL,
    creator_id   integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (creator_id) REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS permission_application_permission_mapping
(
    permission_application_id integer NOT NULL,
    permission_id             integer NOT NULL,
    PRIMARY KEY (permission_application_id, permission_id),
    FOREIGN KEY (permission_application_id) REFERENCES permission_application,
    FOREIGN KEY (permission_id) REFERENCES permission
);

CREATE TABLE IF NOT EXISTS subscription_plan
(
    id            integer GENERATED ALWAYS AS IDENTITY,
    name          text        NOT NULL,
    billing_cycle text        NOT NULL,
    price         numeric     NOT NULL,
    created_time  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS "order"
(
    id           integer GENERATED ALWAYS AS IDENTITY,
    status       text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
