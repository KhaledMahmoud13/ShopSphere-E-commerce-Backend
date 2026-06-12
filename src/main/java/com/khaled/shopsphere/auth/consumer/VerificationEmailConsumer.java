package com.khaled.shopsphere.auth.consumer;

import com.khaled.shopsphere.auth.message.VerificationEmailMessage;
import com.khaled.shopsphere.config.RabbitConfig;
import com.khaled.shopsphere.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationEmailConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(VerificationEmailMessage message) {
        log.info("Processing verification email for {}", message.email());

        emailService.sendVerificationEmail(
                message.email(),
                message.code()
        );
    }
}
