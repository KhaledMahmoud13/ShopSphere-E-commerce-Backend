CREATE TABLE CARTS
(
    ID                 UUID      NOT NULL,
    USER_ID            UUID      NOT NULL,

    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID      NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_carts PRIMARY KEY (ID),
    CONSTRAINT uk_carts_user_id UNIQUE (USER_ID),
    CONSTRAINT fk_carts_users FOREIGN KEY (USER_ID) REFERENCES USERS (ID) ON DELETE CASCADE
);

CREATE TABLE CART_ITEMS
(
    ID                 UUID      NOT NULL,
    CART_ID            UUID      NOT NULL,
    PRODUCT_ID         UUID      NOT NULL,

    QUANTITY           INT       NOT NULL,

    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID      NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_cart_items PRIMARY KEY (ID),
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (CART_ID) REFERENCES CARTS (ID) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (ID) ON DELETE CASCADE
);

CREATE INDEX idx_cart_items_cart_id ON CART_ITEMS (CART_ID);
CREATE INDEX idx_cart_items_product_id ON CART_ITEMS (PRODUCT_ID);
CREATE INDEX idx_carts_user_id ON CARTS (USER_ID);