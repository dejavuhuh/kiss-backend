DROP MATERIALIZED VIEW IF EXISTS user_api_permissions_mv;
DROP TABLE IF EXISTS export_task;
DROP TABLE IF EXISTS big_data;
DROP TABLE IF EXISTS permission_api_mapping;
DROP TABLE IF EXISTS api;
DROP TABLE IF EXISTS api_group;
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS subscription_plan;
DROP TABLE IF EXISTS permission_application_permission_mapping;
DROP TABLE IF EXISTS permission_application;
DROP TABLE IF EXISTS config_history;
DROP TABLE IF EXISTS config;
DROP TABLE IF EXISTS migration_history;
DROP TABLE IF EXISTS issue;
DROP TABLE IF EXISTS user_role_mapping;
DROP TABLE IF EXISTS role_permission_mapping;
DROP TABLE IF EXISTS permission_audit_log;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS session_history;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS feishu_user;
DROP TABLE IF EXISTS "user";

CREATE TABLE IF NOT EXISTS "user"
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    display_name text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feishu_user
(
    id      text    NOT NULL PRIMARY KEY,
    user_id integer NOT NULL REFERENCES "user"
);

CREATE TABLE IF NOT EXISTS account
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      integer     NOT NULL REFERENCES "user",
    username     text        NOT NULL,
    password     text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS session
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token        text        NOT NULL,
    user_id      integer     NOT NULL REFERENCES "user",
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_time timestamptz NOT NULL,
    UNIQUE (token)
);

CREATE TABLE IF NOT EXISTS session_history
(
    id           integer     NOT NULL PRIMARY KEY,
    user_id      integer     NOT NULL REFERENCES "user",
    reason       text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         text        NOT NULL,
    description  text        NULL,
    creator_id   integer     NOT NULL REFERENCES "user",
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS permission
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id    integer     NULL REFERENCES permission,
    type         text        NOT NULL,
    code         text        NOT NULL,
    name         text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS permission_audit_log
(
    id                integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    permission_id     integer     NOT NULL,
    operation         text        NOT NULL,
    operation_details jsonb       NULL,
    operator_id       integer     NOT NULL REFERENCES "user",
    created_time      timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_permission_mapping
(
    role_id       integer NOT NULL REFERENCES role,
    permission_id integer NOT NULL REFERENCES permission,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_role_mapping
(
    user_id integer NOT NULL REFERENCES "user",
    role_id integer NOT NULL REFERENCES role,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS issue
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         text        NOT NULL,
    description   text        NOT NULL,
    trace_id      text        NOT NULL,
    request       jsonb       NOT NULL,
    state         text        NOT NULL,
    related_to_id integer     NULL REFERENCES issue,
    creator_id    integer     NOT NULL REFERENCES "user",
    created_time  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS migration_history
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    version      integer     NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (version)
);

CREATE TABLE IF NOT EXISTS config
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         text        NOT NULL,
    yaml         text        NULL,
    version      integer     NOT NULL,
    creator_id   integer     NOT NULL REFERENCES "user",
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS config_history
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    config_id    integer     NOT NULL REFERENCES config ON DELETE CASCADE,
    yaml         text        NULL,
    reason       text        NOT NULL,
    creator_id   integer     NOT NULL REFERENCES "user",
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permission_application
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reason       text        NOT NULL,
    creator_id   integer     NOT NULL REFERENCES "user",
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permission_application_permission_mapping
(
    permission_application_id integer NOT NULL REFERENCES permission_application,
    permission_id             integer NOT NULL REFERENCES permission,
    PRIMARY KEY (permission_application_id, permission_id)
);

CREATE TABLE IF NOT EXISTS subscription_plan
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          text        NOT NULL,
    billing_cycle text        NOT NULL,
    price         numeric     NOT NULL,
    created_time  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS "order"
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status       text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_category
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id    integer     NULL REFERENCES product_category,
    name         text        NOT NULL,
    is_leaf      boolean     NOT NULL,
    sort_order   integer     NOT NULL,
    enabled      boolean     NOT NULL,
    banner       text        NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE NULLS NOT DISTINCT (parent_id, name),
    CHECK ( is_leaf = FALSE OR banner IS NOT NULL )
);

CREATE TABLE IF NOT EXISTS api_group
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS api
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    group_id     integer     NOT NULL REFERENCES api_group,
    name         text        NOT NULL,
    method       text        NOT NULL,
    path         text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (method, path)
);

CREATE TABLE IF NOT EXISTS permission_api_mapping
(
    permission_id integer NOT NULL REFERENCES permission,
    api_id        integer NOT NULL REFERENCES api,
    PRIMARY KEY (permission_id, api_id)
);

CREATE TABLE IF NOT EXISTS big_data
(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    a  text NOT NULL,
    b  text NOT NULL,
    c  text NOT NULL,
    d  text NOT NULL,
    e  text NOT NULL,
    f  text NOT NULL,
    g  text NOT NULL,
    h  text NOT NULL,
    i  text NOT NULL,
    j  text NOT NULL
);

CREATE TABLE IF NOT EXISTS export_task
(
    id           integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    scene        text        NOT NULL,
    status       text        NOT NULL,
    creator_id   integer     NOT NULL REFERENCES "user",
    trace_id     text        NOT NULL,
    created_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 物化视图
CREATE MATERIALIZED VIEW user_api_permissions_mv AS
SELECT DISTINCT ROW_NUMBER() OVER (ORDER BY u.id, a.id) AS id,
                u.id                                    AS user_id,
                a.method                                AS api_method,
                a.path                                  AS api_path
FROM "user" AS u
         JOIN
     user_role_mapping AS urm ON u.id = urm.user_id
         JOIN
     role_permission_mapping AS rpm ON urm.role_id = rpm.role_id
         JOIN
     permission_api_mapping AS pam ON rpm.permission_id = pam.permission_id
         JOIN
     api AS a ON pam.api_id = a.id;
CREATE UNIQUE INDEX idx_user_api_permissions_mv
    ON user_api_permissions_mv (user_id, api_method, api_path);
