CREATE TABLE ORDERS
(
    ID                 UUID           NOT NULL,
    USER_ID            UUID           NOT NULL,
    TOTAL_PRICE        DECIMAL(10, 2) NOT NULL,
    STATUS             VARCHAR(50)    NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID           NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_orders PRIMARY KEY (ID),
    CONSTRAINT fk_order_user FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE ORDER_ADDRESSES
(
    ID                 UUID         NOT NULL,
    ORDER_ID           UUID         NOT NULL UNIQUE,

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

    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID         NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_order_addresses PRIMARY KEY (ID),
    CONSTRAINT fk_order_addresses_order FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID) ON DELETE CASCADE
);

CREATE TABLE ORDER_ITEMS
(
    ID                 UUID           NOT NULL,
    ORDER_ID           UUID           NOT NULL,
    PRODUCT_ID         UUID           NOT NULL,
    PRODUCT_NAME       VARCHAR(255)   NOT NULL,

    QUANTITY           INT            NOT NULL,
    PRICE_AT_PURCHASE  DECIMAL(10, 2) NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID           NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_order_items PRIMARY KEY (ID),
    CONSTRAINT fk_order_items_order FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID) ON DELETE CASCADE
);

CREATE INDEX idx_orders_user_id ON ORDERS (USER_ID);
CREATE INDEX idx_orders_status ON ORDERS (STATUS);
CREATE INDEX idx_order_items_order_id ON ORDER_ITEMS (ORDER_ID);
CREATE INDEX idx_order_items_product_id ON ORDER_ITEMS (PRODUCT_ID);