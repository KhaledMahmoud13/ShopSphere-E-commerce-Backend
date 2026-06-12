package com.khaled.shopsphere.email.impl;

import com.khaled.shopsphere.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromAddress;

    @Value("${app.email.from-name}")
    private String fromName;

    @Override
    public void sendVerificationEmail(String email, String code) {
        log.info("Sending verification code {} to {}", code, email);
        try {
            String html = renderVerificationTemplate(email, code);
            sendHtml(email, "Verify your ShopSphere account", html);
            log.info("Verification email sent to {}", email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void sendHtml(String to, String subject, String html) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    private String renderVerificationTemplate(String email, String code) {
        Context ctx = new Context();
        ctx.setVariable("email", email);
        ctx.setVariable("code", code);
        ctx.setVariable("expiryMinutes", 10);
        ctx.setVariable("year", java.time.Year.now().getValue());
        return templateEngine.process("email/verification", ctx);
    }
}
