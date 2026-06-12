CREATE TABLE ADDRESSES
(
    ID                 UUID         NOT NULL,
    USER_ID            UUID         NOT NULL,

    RECIPIENT_NAME     VARCHAR(255) NOT NULL,
    RECIPIENT_PHONE    VARCHAR(255) NOT NULL,

    COUNTRY            VARCHAR(255) NOT NULL,
    CITY               VARCHAR(255) NOT NULL,
    AREA               VARCHAR(255) NOT NULL,
    STREET             VARCHAR(255) NOT NULL,

    BUILDING_NUMBER    VARCHAR(100) NOT NULL,
    FLOOR_NUMBER       VARCHAR(100),
    APARTMENT_NUMBER   VARCHAR(100),

    POSTAL_CODE        VARCHAR(100),

    IS_DEFAULT         BOOLEAN      NOT NULL DEFAULT FALSE,

    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID         NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_addresses PRIMARY KEY (ID),

    CONSTRAINT fk_addresses_user
        FOREIGN KEY (USER_ID)
            REFERENCES USERS (ID)
            ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user
    ON ADDRESSES (USER_ID);