package com.khaled.shopsphere.auth.event;

import com.khaled.shopsphere.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationEmailListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handle(VerificationEmailEvent event) {
        emailService.sendVerificationEmail(event.email(), event.code());
    }
}
