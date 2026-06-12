-- PRODUCTS
CREATE TABLE PRODUCTS
(
    id                 UUID           NOT NULL,
    name               VARCHAR(255)   NOT NULL,
    description        TEXT,
    price              DECIMAL(10, 2) NOT NULL,
    stock              INT            NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID   NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_products PRIMARY KEY (id)
);

-- PRODUCT_IMAGES
CREATE TABLE PRODUCT_IMAGES
(
    id                 UUID         NOT NULL,

    PRODUCT_ID         UUID         NOT NULL,

    URL                TEXT         NOT NULL,
    public_id          VARCHAR(255) NOT NULL,
    is_primary         BOOLEAN DEFAULT FALSE,

    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_product_images PRIMARY KEY (id),

    CONSTRAINT FK_PRODUCT_IMAGE_PRODUCT
        FOREIGN KEY (PRODUCT_ID)
            REFERENCES PRODUCTS (ID)
            ON DELETE CASCADE
);

CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_price ON products (price);
CREATE INDEX idx_products_stock ON products (stock);
CREATE INDEX idx_products_created_at ON products (created_date);