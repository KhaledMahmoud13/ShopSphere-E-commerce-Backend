CREATE TABLE OUTBOX_EVENTS
(
    ID             UUID         NOT NULL,

    AGGREGATE_TYPE VARCHAR(100) NOT NULL,
    EVENT_TYPE     VARCHAR(100) NOT NULL,

    PAYLOAD        JSONB        NOT NULL,
    STATUS         VARCHAR(50)  NOT NULL,
    ATTEMPTS       INTEGER      NOT NULL DEFAULT 0,

    SENT_AT        TIMESTAMP,
    CREATED_AT     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_outbox_events PRIMARY KEY (ID)
);

CREATE INDEX idx_outbox_status_created_at
    ON outbox_events (status, created_at);

CREATE INDEX idx_outbox_event_type
    ON outbox_events (event_type);