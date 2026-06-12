package com.khaled.shopsphere.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.shopsphere.auth.message.VerificationEmailMessage;
import com.khaled.shopsphere.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {
    private static final int MAX_ATTEMPTS = 5;

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void poll() {
        List<OutboxEvent> pending = outboxRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pending) {
            try {

                switch (event.getEventType()) {
                    case VERIFICATION_EMAIL -> {
                        VerificationEmailMessage message =
                                objectMapper.readValue(
                                        event.getPayload(),
                                        VerificationEmailMessage.class
                                );

                        rabbitTemplate.convertAndSend(
                                RabbitConfig.EXCHANGE,
                                RabbitConfig.ROUTING_KEY,
                                message
                        );
                    }
                    default -> throw new IllegalStateException("Unknown event type: " + event.getEventType());
                }

                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                log.info("Outbox event {} published", event.getId());
            } catch (Exception e) {
                int attempts = event.getAttempts() + 1;
                event.setAttempts(attempts);

                if (attempts >= MAX_ATTEMPTS) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event {} failed after {} attempts", event.getId(), attempts);
                } else {
                    log.warn("Outbox event {} failed (attempt {}), will retry",
                            event.getId(), attempts);
                }
            }
        }
    }
}
