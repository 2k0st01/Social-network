package com.example.authentication_client.utils.emailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService
implements EmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("no.reply.test.kosto@gmail.com");
            this.mailSender.send(mimeMessage);
        }
        catch (MessagingException e) {
            LOGGER.error("fail to send email: " + email, (Throwable)e);
            throw new IllegalStateException("failed to send email");
        }
    }

    @Generated
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
