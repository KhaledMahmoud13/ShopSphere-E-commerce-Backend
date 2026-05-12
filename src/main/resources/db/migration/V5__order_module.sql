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

CREATE TABLE ORDER_ITEMS
(
    ID                 UUID           NOT NULL,
    ORDER_ID           UUID           NOT NULL,
    PRODUCT_ID         UUID           NOT NULL,
    PRODUCT_NAME       VARCHAR(255)   NOT NULL,
    IMAGE_URL          TEXT,

    QUANTITY           INT            NOT NULL,
    PRICE_AT_PURCHASE  DECIMAL(10, 2) NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID           NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_order_items PRIMARY KEY (ID),
    CONSTRAINT fk_order_items_order FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID) ON DELETE CASCADE
);