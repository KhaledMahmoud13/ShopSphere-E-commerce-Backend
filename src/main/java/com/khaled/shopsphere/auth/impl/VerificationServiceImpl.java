package com.khaled.shopsphere.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khaled.shopsphere.auth.VerificationService;
import com.khaled.shopsphere.auth.message.VerificationEmailMessage;
import com.khaled.shopsphere.auth.request.SendCodeRequest;
import com.khaled.shopsphere.auth.request.VerifyEmailRequest;
import com.khaled.shopsphere.outbox.EventType;
import com.khaled.shopsphere.outbox.OutboxEvent;
import com.khaled.shopsphere.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {
    private static final String PREFIX = "verify:code:";
    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    //    private final ApplicationEventPublisher eventPublisher;
//    private final VerificationEmailProducer producer;

    @Override
    @Transactional
    public void sendVerificationCode(SendCodeRequest request) {
        String code = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        redisTemplate.opsForValue()
                .set(
                        PREFIX + request.getEmail(),
                        code,
                        Duration.ofMinutes(10)
                );

//        eventPublisher.publishEvent(new VerificationEmailEvent(request.getEmail(), code));
//        producer.send(request.getEmail(), code);

        try {
            VerificationEmailMessage message = new VerificationEmailMessage(
                    request.getEmail(),
                    code
            );

            String payload = objectMapper.writeValueAsString(message);

            outboxRepository.save(OutboxEvent.builder()
                    .aggregateType("USER")
                    .eventType(EventType.VERIFICATION_EMAIL)
                    .payload(payload)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }

    @Override
    public boolean verifyCode(VerifyEmailRequest request) {
        String storedCode = redisTemplate.opsForValue().get(PREFIX + request.getEmail());

        if (storedCode == null || !storedCode.equals(request.getCode())) {
            return false;
        }

        redisTemplate.delete(PREFIX + request.getEmail());

        return true;
    }
}
