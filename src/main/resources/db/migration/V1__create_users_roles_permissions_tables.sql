CREATE TABLE PERMISSIONS
(
    id                 UUID         NOT NULL,
    name               VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID NOT NULL,
    last_modified_by   UUID,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name)
);

CREATE TABLE ROLES
(
    id                 UUID         NOT NULL,
    name               VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID NOT NULL,
    last_modified_by   UUID,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE ROLE_PERMISSIONS
(
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES ROLES (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES PERMISSIONS (id)
            ON DELETE CASCADE
);

CREATE TABLE USERS
(
    id                    UUID         NOT NULL,
    first_name            VARCHAR(255) NOT NULL,
    last_name             VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    phone_number          VARCHAR(255) NOT NULL,
    password              VARCHAR(255) NOT NULL,
    is_enabled            BOOLEAN      NOT NULL DEFAULT FALSE,
    is_account_locked     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_credential_expired BOOLEAN      NOT NULL DEFAULT FALSE,
    is_email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_phone_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_date          TIMESTAMP    NOT NULL,
    last_modified_date    TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_phone_number UNIQUE (phone_number)
);

CREATE TABLE USER_ROLES
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES USERS (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES ROLES (id)
            ON DELETE CASCADE
);