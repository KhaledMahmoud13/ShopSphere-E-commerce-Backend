CREATE TABLE Payments
(
    ID                 UUID           NOT NULL,
    ORDER_ID           UUID           NOT NULL,
    AMOUNT             DECIMAL(10, 2) NOT NULL,
    STATUS             VARCHAR(50)    NOT NULL,

    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         UUID           NOT NULL,
    last_modified_by   UUID,

    CONSTRAINT pk_payments PRIMARY KEY (ID),
    CONSTRAINT uk_payment_order UNIQUE (ORDER_ID),
    CONSTRAINT fk_payment_order FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID)
);