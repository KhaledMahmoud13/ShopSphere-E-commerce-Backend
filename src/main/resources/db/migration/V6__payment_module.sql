CREATE TABLE PAYMENTS
(
    ID                       UUID           NOT NULL,

    ORDER_ID                 UUID           NOT NULL,

    AMOUNT                   DECIMAL(10, 2) NOT NULL,

    STATUS                   VARCHAR(50)    NOT NULL,

    STRIPE_SESSION_ID        VARCHAR(255),
    STRIPE_PAYMENT_INTENT_ID VARCHAR(255),

    PAID_AT                  TIMESTAMP,

    created_date             TIMESTAMP      NOT NULL,
    last_modified_date       TIMESTAMP,
    created_by               UUID           NOT NULL,
    last_modified_by         UUID,

    CONSTRAINT pk_payments PRIMARY KEY (ID),

    CONSTRAINT uk_payments_order UNIQUE (ORDER_ID),

    CONSTRAINT uk_payments_session UNIQUE (STRIPE_SESSION_ID),

    CONSTRAINT uk_payments_payment_intent UNIQUE (STRIPE_PAYMENT_INTENT_ID),

    CONSTRAINT fk_payments_order FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID) ON DELETE CASCADE
);

CREATE INDEX idx_payments_order_id ON PAYMENTS (ORDER_ID);

CREATE INDEX idx_payments_status ON PAYMENTS (STATUS);

CREATE INDEX idx_payments_paid_at ON PAYMENTS (PAID_AT);

CREATE INDEX idx_payments_created_date ON PAYMENTS (created_date);