CREATE TABLE CATEGORIES
(
    ID                 UUID         NOT NULL,
    NAME               VARCHAR(255) NOT NULL UNIQUE,

    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID         NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_categories PRIMARY KEY (ID)
);

ALTER TABLE PRODUCTS
    ADD COLUMN CATEGORY_ID UUID;

ALTER TABLE PRODUCTS
    ADD CONSTRAINT fk_product_category
        FOREIGN KEY (CATEGORY_ID)
            REFERENCES CATEGORIES (ID);

CREATE INDEX idx_products_category_id
    ON PRODUCTS (CATEGORY_ID);