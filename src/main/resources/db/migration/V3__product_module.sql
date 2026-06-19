CREATE TABLE PRODUCTS
(
    ID                 UUID           NOT NULL,
    NAME               VARCHAR(255)   NOT NULL,
    DESCRIPTION        TEXT,
    PRICE              DECIMAL(10, 2) NOT NULL,
    STOCK              INT            NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID           NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_products PRIMARY KEY (ID)
);

CREATE TABLE PRODUCT_IMAGES
(
    ID                 UUID         NOT NULL,

    PRODUCT_ID         UUID         NOT NULL,

    URL                TEXT         NOT NULL,
    PUBLIC_ID          VARCHAR(255) NOT NULL,
    IS_PRIMARY         BOOLEAN DEFAULT FALSE,

    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID         NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_product_images PRIMARY KEY (ID),

    CONSTRAINT FK_PRODUCT_IMAGE_PRODUCT
        FOREIGN KEY (PRODUCT_ID)
            REFERENCES PRODUCTS (ID)
            ON DELETE CASCADE
);

CREATE INDEX idx_products_name ON products (NAME);
CREATE INDEX idx_products_price ON products (PRICE);
CREATE INDEX idx_products_stock ON products (STOCK);
CREATE INDEX idx_products_created_at ON products (created_date);