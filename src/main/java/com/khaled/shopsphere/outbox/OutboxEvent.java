package com.khaled.shopsphere.outbox;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "OUTBOX_EVENTS")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "AGGREGATE_TYPE", nullable = false)
    private String aggregateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVENT_TYPE", nullable = false)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "PAYLOAD", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "ATTEMPTS")
    @Builder.Default
    private int attempts = 0;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
