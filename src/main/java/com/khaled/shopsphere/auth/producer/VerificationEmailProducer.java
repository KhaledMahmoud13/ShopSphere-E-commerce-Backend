package com.khaled.shopsphere.auth.producer;

import com.khaled.shopsphere.auth.message.VerificationEmailMessage;
import com.khaled.shopsphere.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(String email, String code) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                new VerificationEmailMessage(email, code)
        );
    }
}
